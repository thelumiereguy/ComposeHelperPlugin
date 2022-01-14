package com.thelumiereguy.compose_helper.intention.data.composable_finder

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

class ParentComposableFinder : ComposableFunctionFinder {

    override fun isFunctionComposable(psiElement: PsiElement): Boolean {
        if (psiElement is KtCallExpression || psiElement.parent is KtCallExpression) {
            psiElement.getChildOfType<KtLambdaArgument>()?.let { lambdaChild ->
                return getComposableFromChildLambda(lambdaChild)
            }

            psiElement.parent.getChildOfType<KtLambdaArgument>()?.let { lambdaChild ->
                return getComposableFromChildLambda(lambdaChild)
            }
        }

        return false
    }

    private fun getComposableFromChildLambda(lambdaArgument: KtLambdaArgument): Boolean {
        val children = lambdaArgument.getLambdaExpression()?.functionLiteral?.bodyExpression?.children

        return children?.any { psiElement ->
            if (psiElement is KtCallExpression) {
                detectComposableFromCallExpression(psiElement)
            } else
                false
        } ?: false
    }
}
