package com.thelumiereguy.composeplugin.core.composable_function_finder

import com.intellij.psi.PsiElement

interface ComposableFunctionFinder {
    fun isFunctionComposable(psiElement: PsiElement): Boolean
}
