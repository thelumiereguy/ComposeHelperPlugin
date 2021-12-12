package com.thelumiereguy.composeplugin.intention.data.composable_finder

import com.intellij.psi.PsiElement

interface ComposableFunctionFinder {
    fun isFunctionComposable(psiElement: PsiElement): Boolean
}
