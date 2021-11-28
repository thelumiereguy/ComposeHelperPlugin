package com.thelumiereguy.composeplugin.intention

//import com.intellij.psi.PsiElement
//import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class ComposableWrapper : PsiElementBaseIntentionAction(), IntentionAction {

    override fun getFamilyName(): String {
        return "ComposableIntention";
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element == null) {
            return false
        }


        println("language ${element.language}")

        if (element.language != Language.findLanguageByID("kotlin")) { //Compose is for Kotlin
            return false
        }

        println("element ${element is KtNameReferenceExpression}")

        if (element is KtNameReferenceExpression) {
            element.reference?.let { reference ->
                println("${reference.resolve()}")
            }
        }
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {

    }

}
