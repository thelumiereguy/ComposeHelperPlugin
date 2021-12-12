package com.thelumiereguy.composeplugin.intention.data.get_root_element

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

        return when (element) {
            is KtProperty -> element
            is KtNameReferenceExpression,
            is KtValueArgumentList -> invoke(element.parent)
            is KtDotQualifiedExpression,
            is KtCallExpression -> {
                when (element.parent) {
                    is KtProperty,
                    is KtDotQualifiedExpression -> invoke(element.parent)  //composable dot expression
                    is KtPropertyDelegate -> invoke(element.parent.parent)  //composable dot expression
                    else -> element
                }
            }
            else -> element
        }
    }
}
