package com.thelumiereguy.composeplugin.intention.presentation.intentions

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.refactoring.RefactoringActionHandlerFactory
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.thelumiereguy.composeplugin.intention.data.composable_finder.ComposableFunctionFinder
import com.thelumiereguy.composeplugin.intention.data.composable_finder.DeepComposableFunctionFinderImpl
import com.thelumiereguy.composeplugin.intention.data.composable_wrapper.ComposableWrapper
import com.thelumiereguy.composeplugin.intention.data.composable_wrapper.ProvidesComposableTemplate
import com.thelumiereguy.composeplugin.intention.data.get_root_element.GetRootElement
import com.thelumiereguy.composeplugin.intention.presentation.icons.SdkIcons
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.editor.fixers.startLine
import org.jetbrains.kotlin.psi.KtPsiFactory
import javax.swing.Icon


class WrapWithComposableIntention : PsiElementBaseIntentionAction(), ProvidesComposableTemplate, Iconable {

    override fun getText(): String {
        return "Wrap with Composable"
    }

    override fun getFamilyName(): String {
        return "ComposableIntention"
    }

    private val composableFunctionFinder: ComposableFunctionFinder = DeepComposableFunctionFinderImpl()

    private val getRootElement = GetRootElement()

    private val composableWrapper = ComposableWrapper(this)



    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element == null) {
            return false
        }

        if (element.language.id != KotlinLanguage.INSTANCE.id) { //Compose is for Kotlin
            return false
        }

        if (!element.isWritable) {
            return false
        }

        return element.parent?.let { parentPsiElement ->
            composableFunctionFinder.isFunctionComposable(parentPsiElement)
        } ?: false
    }

    private val fact = RefactoringActionHandlerFactory.getInstance()
    private val rename = fact.createRenameHandler()

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {

        getRootElement(element.parent)?.let { rootElement ->
            val lineNumber = element.startLine(editor!!.document)


//            CommandProcessor.getInstance().executeCommand(
//                project, {
            val newElement = KtPsiFactory(project).createExpression(
                composableTemplatePrefix +
                        rootElement.text +
                        composableTemplateSuffix
            )

            rootElement.replace(newElement)

            CodeStyleManager.getInstance(project).reformat(newElement)

            val primaryCaret = editor.caretModel.primaryCaret

            println("${rootElement.textOffset} ${rootElement.startOffset} ${rootElement.endOffset}")

            primaryCaret.moveToLogicalPosition(
                LogicalPosition(
                    lineNumber,
                    rootElement.startOffset
                )
            )



//            newElement.children.find {
//                it is KtNameReferenceExpression
//            }?.let { composableName ->
//
//
//            }

//                }, "",
//                "composeplugin"
//            )
        }
    }

    override val composableTemplatePrefix = "Box(modifier = Modifier) {"

    override val composableTemplateSuffix = "}"

    override fun getIcon(flags: Int): Icon = SdkIcons.composeIcon
}
