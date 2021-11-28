package com.thelumiereguy.composeplugin.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.toUElement

class ComposableWrapper : PsiElementBaseIntentionAction(), IntentionAction {

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

        val parent = element.parent.toUElement()

        if (parent is USimpleNameReferenceExpression) {

            val composableDeclaration = parent.resolve()

            if (composableDeclaration is PsiMethod) {
                println("element ${composableDeclaration.text}")

                return composableDeclaration.annotations.any { annotation ->

                    annotation.hasQualifiedName("androidx.compose.runtime.Composable")
                }
            }
        }
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {

    }

}
