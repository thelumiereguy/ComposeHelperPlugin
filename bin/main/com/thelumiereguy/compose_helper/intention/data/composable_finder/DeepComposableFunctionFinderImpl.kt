package com.thelumiereguy.compose_helper.intention.data.composable_finder

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.toUElement

class DeepComposableFunctionFinderImpl : ComposableFunctionFinder {

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

        if (psiElement is KtValueArgumentList) {
            val parent = psiElement.parent
            if (parent is KtCallExpression) {
                return detectComposableFromCallExpression(parent)
            }
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


}
