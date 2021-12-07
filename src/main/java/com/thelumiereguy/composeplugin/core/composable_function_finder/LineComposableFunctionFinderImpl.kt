package com.thelumiereguy.composeplugin.core.composable_function_finder

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.toUElement

class LineComposableFunctionFinderImpl : ComposableFunctionFinder {
    override fun isFunctionComposable(psiElement: PsiElement): Boolean {

        val parent = psiElement.parent?.toUElement()

        if (parent is USimpleNameReferenceExpression) {

            val composableDeclaration = parent.resolve()

            if (composableDeclaration is PsiMethod) {

                val isComposable = composableDeclaration.annotations.any { annotation ->

                    annotation.hasQualifiedName("androidx.compose.runtime.Composable")
                }

                return isComposable
            }
        }
        return false
    }
}
