package com.thelumiereguy.compose_helper.intention.actions

import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.thelumiereguy.compose_helper.intention.icons.SdkIcons
import com.thelumiereguy.compose_helper.intention.actions.utils.composableFinder.ChildComposableFinder
import com.thelumiereguy.compose_helper.intention.actions.utils.composableFinder.ComposableFunctionFinder
import com.thelumiereguy.compose_helper.intention.actions.utils.getRootPsiElement.GetRootPsiElement
import com.thelumiereguy.compose_helper.intention.actions.utils.isIntentionAvailable
import org.jetbrains.kotlin.psi.KtCallExpression
import javax.swing.Icon

class RemoveParentComposableIntention :
    PsiElementBaseIntentionAction(),
    Iconable,
    PriorityAction {

    override fun getText(): String {
        return "Remove the parent Composable"
    }

    override fun getFamilyName(): String {
        return "Compose helper actions"
    }

    private val getRootElement = GetRootPsiElement()

    private val composableFunctionFinder: ComposableFunctionFinder = ChildComposableFinder()

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return element.isIntentionAvailable(composableFunctionFinder)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val callExpression = getRootElement(element.parent) as? KtCallExpression ?: return
        val lambdaBlock =
            callExpression.lambdaArguments.firstOrNull()?.getLambdaExpression()?.functionLiteral?.bodyExpression
                ?: return
        callExpression.replace(lambdaBlock)
    }

    override fun getPriority(): PriorityAction.Priority {
        return PriorityAction.Priority.NORMAL
    }

    override fun getIcon(flags: Int): Icon = SdkIcons.composeIcon
}
