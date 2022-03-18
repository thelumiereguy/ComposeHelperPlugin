package com.thelumiereguy.compose_helper.intention.presentation.intentions

import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.thelumiereguy.compose_helper.intention.data.composable_finder.ComposableFunctionFinder
import com.thelumiereguy.compose_helper.intention.data.composable_finder.DeepComposableFunctionFinderImpl
import com.thelumiereguy.compose_helper.intention.data.composable_finder.ParentComposableFinder
import com.thelumiereguy.compose_helper.intention.data.get_root_element.GetRootElement
import com.thelumiereguy.compose_helper.intention.presentation.icons.SdkIcons
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import javax.swing.Icon

class ExtractComposableIntention : PsiElementBaseIntentionAction(), Iconable, PriorityAction {

    override fun getText(): String {
        return "Extract composable"
    }

    override fun getFamilyName(): String {
        return "Compose helper actions"
    }

    private val getRootElement = GetRootElement()

    private val composableFunctionFinder: ComposableFunctionFinder = DeepComposableFunctionFinderImpl()

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
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
        getRootElement(element.parent)?.delete()
    }

    override fun getIcon(flags: Int): Icon = SdkIcons.composeIcon

    override fun getPriority(): PriorityAction.Priority {
        return PriorityAction.Priority.NORMAL
    }
}
