package com.thelumiereguy.compose_helper.intention.actions.utils.composableFinder

import com.intellij.psi.PsiElement

interface ComposableFunctionFinder {
    fun isFunctionComposable(psiElement: PsiElement): Boolean
}