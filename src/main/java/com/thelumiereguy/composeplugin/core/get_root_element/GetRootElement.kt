package com.thelumiereguy.composeplugin.core.get_root_element

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*

/**
 *  KtValueArgumentList -> Parent -> KtNameReferenceExpression -> Parent -> KtCallExpression -> Parent -> KtPropertyDelegate -> Parent -> Property
 *  KtNameReferenceExpression -> Parent -> KtCallExpression ->  Parent -> KtDotQualifiedExpression -> Parent -> KtPropertyDelegate ->  Property
 *  KtNameReferenceExpression -> Parent -> KtCallExpression -> Parent -> KtPropertyDelegate -> Parent -> Property
 *  KtNameReferenceExpression -> Parent -> KtCallExpression -> Parent -> Property
 *  KtNameReferenceExpression -> Parent -> KtCallExpression
 **/

class GetRootElement {

    /**
     * Composable can be CallExpression (Composable Function) or Property (Composable Property like remember)
     */
    operator fun invoke(element: PsiElement): PsiElement? {
        //for normal composables
        if (element is KtNameReferenceExpression) {
            return if (element.parent is KtCallExpression) {
                invoke(element.parent)
            } else element
        }

        if (element is KtCallExpression) {
            return when (element.parent) {
                is KtProperty,   // for composable properties
                is KtDotQualifiedExpression -> invoke(element.parent)  //composable dot expression
                is KtPropertyDelegate -> invoke(element.parent.parent)  //composable dot expression
                else -> element
            }
        }

        if (element is KtValueArgumentList) {
            return invoke(element.parent)
        }


        if (element is KtDotQualifiedExpression) {
            return if (element.parent is KtPropertyDelegate) {
                invoke(element.parent.parent)
            } else element
        }

        if (element is KtProperty) {
            return element
        }

        return null
    }
}
