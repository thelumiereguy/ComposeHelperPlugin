package com.thelumiereguy.composeplugin.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.thelumiereguy.composeplugin.core.composable_function_finder.ComposableFunctionFinder
import com.thelumiereguy.composeplugin.core.composable_function_finder.ComposableFunctionFinder2Impl
import com.thelumiereguy.composeplugin.core.get_root_element.GetRootElement
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.uast.toUElement

class ComposableWrapper : PsiElementBaseIntentionAction(), IntentionAction {

    private val composableFunctionFinder: ComposableFunctionFinder = ComposableFunctionFinder2Impl()

    override fun getText(): String {
        return "Wrap with Composable"
    }

    override fun getFamilyName(): String {
        return "ComposableIntention"
    }

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

    private val getRootElement = GetRootElement()

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {

        CommandProcessor.getInstance().executeCommand(
            project,
            {

                getRootElement(element.parent)?.let { rootElement ->
                    val file = KtPsiFactory(project).createExpression(
                        """
                                Row(modifier = Modifier) {
                                    item {
                                    ${rootElement.text}
                                    }
                                }
                            """.trimIndent().trim()
                    )

                    rootElement.replace(file)

                    CodeStyleManager.getInstance(project).reformat(file)

                    println("To be inserted ${file.text}")
                }

            }, "",
            "composeplugin"
        )

    }

}
