package com.thelumiereguy.compose_helper.intention.presentation.dialog

import com.intellij.codeInsight.hint.ParameterInfoTaskRunnerUtil.runTask
import com.intellij.java.refactoring.JavaRefactoringBundle
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.WriteActionAware
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameHelper
import com.intellij.refactoring.RefactoringBundle
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.refactoring.ui.RefactoringDialog
import com.intellij.refactoring.util.CommonRefactoringUtil
import com.intellij.refactoring.util.RefactoringMessageUtil
import com.intellij.ui.RecentsManager
import com.intellij.util.IncorrectOperationException
import com.intellij.util.concurrency.NonUrgentExecutor
import java.awt.BorderLayout
import java.util.concurrent.Callable
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.core.getFqNameByDirectory
import org.jetbrains.kotlin.idea.core.unquote
import org.jetbrains.kotlin.psi.psiUtil.isIdentifier
import org.jetbrains.kotlin.psi.psiUtil.quoteIfNeeded

internal class ExtractInterfaceDialog(
    project: Project,
    private val element: PsiElement,
    private val composableName: String,
    private val sourceFile: PsiFile,
    packageName: String,
) : RefactoringDialog(
    project,
    true
) {
    companion object {
        private const val DESTINATION_PACKAGE_RECENT_KEY = "ExtractSuperBase.RECENT_KEYS"
        private const val KOTLIN_FILE_TYPE = ".${KotlinFileType.EXTENSION}"
        private const val REFACTORING_NAME = "Extract Composable"
    }

    private var destinationPackageField: PackageNameReferenceEditorCombo
    private var destinationFileTextField: JTextField


    init {
        title = REFACTORING_NAME

        destinationFileTextField = JTextField().apply {
            text = "$composableName.${KotlinFileType.EXTENSION}"
        }

        destinationPackageField = PackageNameReferenceEditorCombo(
            packageName,
            myProject,
            DESTINATION_PACKAGE_RECENT_KEY,
            RefactoringBundle.message("choose.destination.package")
        )

        init()
    }

    override fun createCenterPanel(): JComponent? {
        return null
    }

    override fun createNorthPanel(): JComponent {
        return JPanel(BorderLayout()).apply {

            add(Box.createVerticalBox().apply {
                add(
                    JPanel(BorderLayout()).apply {
                        add(
                            JLabel("Extract composable from:"), BorderLayout.NORTH
                        )

                        add(
                            JTextField().apply {
                                isEditable = false
                                text = sourceFile.name
                            },
                            BorderLayout.CENTER
                        )
                    }
                )

                add(Box.createVerticalStrut(10))

                add(
                    JPanel(BorderLayout()).apply {
                        add(
                            JLabel().apply {
                                text = "This will extract the composable into a new file"
                            }, BorderLayout.NORTH
                        )

                        add(
                            destinationFileTextField,
                            BorderLayout.CENTER
                        )
                    }
                )

                add(Box.createVerticalStrut(10))

                add(
                    JPanel(BorderLayout()).apply {
                        add(
                            JLabel().apply {
                                text = "Package for new file:"
                            }, BorderLayout.NORTH
                        )

                        add(
                            destinationPackageField
                        )
                    }
                )
            })
        }
    }

    override fun doAction() {

        val extractedFileName = destinationFileTextField.text

        if (extractedFileName.isEmpty() || extractedFileName.contains(KOTLIN_FILE_TYPE).not()) {
            val error = "Please enter a valid file name with .kt extension"
            CommonRefactoringUtil.showErrorMessage(REFACTORING_NAME, error, helpId, myProject)
            destinationFileTextField.requestFocusInWindow()
            return
        }

        val packageName = destinationPackageField.text.trim()

        if (packageName.isEmpty()) {
            val error = "Package name is empty"
            CommonRefactoringUtil.showErrorMessage(REFACTORING_NAME, error, helpId, myProject)
            destinationPackageField.requestFocusInWindow()
            return
        }

        var isInvalidNameError: String? = isFileNameInvalid(extractedFileName)

        if (isInvalidNameError != null) {
            CommonRefactoringUtil.showErrorMessage(REFACTORING_NAME, isInvalidNameError, helpId, myProject)
            destinationFileTextField.requestFocusInWindow()
            return
        }

        isInvalidNameError = validateQualifiedName(packageName, extractedFileName)

        if (isInvalidNameError != null) {
            CommonRefactoringUtil.showErrorMessage(REFACTORING_NAME, isInvalidNameError, helpId, myProject)
            destinationFileTextField.requestFocusInWindow()
            return
        }

        RecentsManager.getInstance(myProject).registerRecentEntry(DESTINATION_PACKAGE_RECENT_KEY, packageName)


        CommandProcessor.getInstance().executeCommand(
            myProject,
            {
                ReadAction.nonBlocking(Callable {
                    try {
                        preparePackage()
                    } catch (e: IncorrectOperationException) {
                        e.printStackTrace()
                        CommonRefactoringUtil.showErrorMessage(REFACTORING_NAME, e.message, helpId, myProject)
                        destinationPackageField.requestFocusInWindow()
                    } catch (e: OperationFailedException) {
                        e.printStackTrace()
                        CommonRefactoringUtil.showErrorMessage(REFACTORING_NAME, e.message, helpId, myProject)
                        destinationPackageField.requestFocusInWindow()
                    }
                }).expireWith(disposable)
                    .finishOnUiThread(ModalityState.any()) {
                        closeOKAction()
                    }
                    .submit(NonUrgentExecutor.getInstance())
            },
            RefactoringBundle.message("create.directory"),
            null
        )

//        if (!checkConflicts()) return
//
//        executeRefactoring()

    }

    private fun preparePackage() {
        val targetPackageName: String = destinationPackageField.text.trim()
        val containingFile = sourceFile

        val fromDefaultPackage = containingFile is PsiClassOwner && containingFile.packageName.isEmpty()
        if (!(fromDefaultPackage && StringUtil.isEmpty(targetPackageName)) && !PsiNameHelper.getInstance(myProject)
                .isQualifiedName(targetPackageName)
        ) {
            throw OperationFailedException(
                JavaRefactoringBundle.message(
                    "invalid.package.name",
                    targetPackageName
                )
            )
        }

        val aPackage = JavaPsiFacade.getInstance(myProject).findPackage(targetPackageName)

        if (aPackage != null) {
            val directories = aPackage.getDirectories(sourceFile.resolveScope)
        }

        val error = RefactoringMessageUtil.checkCanCreateClass(
            containingFile.containingDirectory,
            destinationFileTextField.text
        )

        if (error != null) {
            throw OperationFailedException(error)
        }
    }

    private fun isFileNameInvalid(name: String): String? {
        return when {
            !name.quoteIfNeeded().isIdentifier() -> RefactoringMessageUtil.getIncorrectIdentifierMessage(name)
            name.unquote() == sourceFile.name -> KotlinBundle.message("error.text.different.name.expected")
            else -> null
        }
    }

    private fun validateQualifiedName(packageName: String?, extractedSuperName: String): String? {
        return if (StringUtil.getQualifiedName(packageName, extractedSuperName) == sourceFile.getFqNameByDirectory()
                .asString()
        ) {
            JavaRefactoringBundle.message("different.name.expected")
        } else null
    }
}

data class OperationFailedException(val errorMessage: String?) : Exception(errorMessage)
