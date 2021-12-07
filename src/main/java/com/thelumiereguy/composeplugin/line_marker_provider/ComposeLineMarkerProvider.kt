package com.thelumiereguy.composeplugin.line_marker_provider;

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.lang.Language
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.thelumiereguy.composeplugin.core.composable_function_finder.ComposableFunctionFinder
import com.thelumiereguy.composeplugin.core.composable_function_finder.LineComposableFunctionFinderImpl
import com.thelumiereguy.composeplugin.icons.SdkIcons

class ComposeLineMarkerProvider : LineMarkerProvider {

    private val composableFunctionFinder: ComposableFunctionFinder = LineComposableFunctionFinderImpl()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element == null) {
            return null
        }

        if (element.language.id != Language.findLanguageByID("kotlin")?.id) { //Compose is for Kotlin
            return null
        }

        if (!element.isWritable) {
            return null
        }

        val isComposable = composableFunctionFinder.isFunctionComposable(element)

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
