package com.thelumiereguy.compose_helper.intention.data.composable_finder

import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.toUElement

interface ComposableFunctionFinder {

    fun isFunctionComposable(psiElement: PsiElement): Boolean

    fun detectComposableFromCallExpression(psiElement: KtCallExpression): Boolean {
        return psiElement.children.any {
            val child = it.toUElement()
            if (child is USimpleNameReferenceExpression) {
                detectComposableFromReferenceExpression(child)
            } else false
        }
    }

    fun detectComposableFromReferenceExpression(uElement: USimpleNameReferenceExpression): Boolean {
        uElement.resolve()?.let { psiElement ->
            if (psiElement is PsiMethod) {
                return CachedValuesManager.getCachedValue(psiElement) {

                    val hasComposableAnnotation = psiElement.annotations.any { annotation ->
                        annotation.hasQualifiedName("androidx.compose.runtime.Composable")
                    }

                    CachedValueProvider.Result.create(
                        hasComposableAnnotation,
                        psiElement.containingFile,
                        ProjectRootModificationTracker.getInstance(psiElement.project)
                    )
                }
            }
        }
        return false
    }
}
