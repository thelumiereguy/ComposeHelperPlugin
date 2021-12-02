package com.thelumiereguy.composeplugin.core.composable_function_finder

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.toUElement

class ComposableFunctionFinder2Impl : ComposableFunctionFinder {

    override fun isFunctionComposable(psiElement: PsiElement): Boolean {

        val uElement = psiElement.toUElement()

        if (uElement is USimpleNameReferenceExpression) {
            return detectComposableFromReferenceExpression(uElement)
        }

        if (psiElement is KtCallExpression) {
            return detectComposableFromCallExpression(psiElement)
        }

        if (psiElement is KtProperty) {
            return detectComposableFromKtProperty(psiElement)
        }

        return false
    }


    private fun detectComposableFromKtProperty(psiElement: KtProperty): Boolean {
        return psiElement.children.any {
            if (it is KtCallExpression) {
                detectComposableFromCallExpression(it)
            } else false
        }
    }

    private fun detectComposableFromCallExpression(psiElement: KtCallExpression): Boolean {
        return psiElement.children.any {
            val child = it.toUElement()
            if (child is USimpleNameReferenceExpression) {
                detectComposableFromReferenceExpression(child)
            } else false
        }
    }

    private fun detectComposableFromReferenceExpression(uElement: USimpleNameReferenceExpression): Boolean {
        uElement.resolve()?.let {
            if (it is PsiMethod) {
                return it.annotations.any { annotation ->
                    annotation.hasQualifiedName("androidx.compose.runtime.Composable")
                }
            }
        }
        return false
    }
}
