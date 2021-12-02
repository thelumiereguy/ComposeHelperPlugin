package com.thelumiereguy.composeplugin.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.thelumiereguy.composeplugin.core.composable_function_finder.ComposableFunctionFinder
import com.thelumiereguy.composeplugin.core.composable_function_finder.ComposableFunctionFinder2Impl
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.uast.USimpleNameReferenceExpression
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

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {

        val uElement = element.parent?.toUElement()

//        CommandProcessor.getInstance().executeCommand(
//            project,
//            {
                println("executeCommand")

                if (uElement is USimpleNameReferenceExpression) {
                    println("USimpleNameReferenceExpression")
                    uElement.sourcePsi?.let {
                        val file = PsiFileFactory.getInstance(project).createFileFromText(
                            KotlinLanguage.INSTANCE,
                            """
                                Box(modifier = Modifier) {
                                
                                }
                            """.trimIndent().trim()
                        )

                        println("To be inserted $file")
                    }
                }

                if (element is KtCallExpression) {

                }

                if (element is KtProperty) {

                }


//            }, "",
//            "composeplugin"
//        )

    }

}
