package com.thelumiereguy.compose_helper.intention.data.composable_finder

import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.jetbrains.kotlin.idea.caches.project.NotUnderContentRootModuleInfo.project
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

    private fun detectComposableFromCallExpression(psiElement: KtCallExpression): Boolean {
        return psiElement.children.any {
            val child = it.toUElement()
            if (child is USimpleNameReferenceExpression) {
                detectComposableFromReferenceExpression(child)
            } else false
        }
    }

    private fun detectComposableFromReferenceExpression(uElement: USimpleNameReferenceExpression): Boolean {
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
