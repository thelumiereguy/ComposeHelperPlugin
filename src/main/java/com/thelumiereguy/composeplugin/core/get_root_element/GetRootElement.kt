package com.thelumiereguy.composeplugin.core.get_root_element

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*

class GetRootElement {

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
                else -> element
            }
        }

        if (element is KtValueArgumentList) {
            return invoke(element.parent)
        }


        if (element is KtDotQualifiedExpression) {
            return if (element.parent is KtPropertyDelegate) {
                invoke(element.parent)
            } else element
        }

        if (element is KtProperty) {
            return element
        }

        return null
    }
}
