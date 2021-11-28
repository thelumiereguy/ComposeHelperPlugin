package com.thelumiereguy.composeplugin.line_marker_provider

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.util.PlatformIcons
import com.thelumiereguy.composeplugin.icons.SdkIcons
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.toUElement

fun getLine(element: PsiElement): LineMarkerInfo<PsiElement>? {
    val parent = element.parent.toUElement()

    if (parent is USimpleNameReferenceExpression) {

        val composableDeclaration = parent.resolve()

        if (composableDeclaration is PsiMethod) {
            println("element ${composableDeclaration.text}")

            val isComposable = composableDeclaration.annotations.any { annotation ->

                annotation.hasQualifiedName("androidx.compose.runtime.Composable")
            }

            return if (isComposable) {
                LineMarkerInfo(
                    element,
                    element.textRange,
                    SdkIcons.gutterIcon,
                    null,
                    null,
                    GutterIconRenderer.Alignment.CENTER
                ) { "Compose Icon" }
            } else null

        }
    }

    return null
}
