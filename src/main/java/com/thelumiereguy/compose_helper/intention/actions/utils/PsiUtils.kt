package com.thelumiereguy.compose_helper.intention.actions.utils

import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.thelumiereguy.compose_helper.intention.actions.utils.composableFinder.ComposableFunctionFinder
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

internal fun KtCallExpression.isComposable(): Boolean {
    return getChildOfType<KtNameReferenceExpression>()?.isComposable() ?: false
}

internal fun KtNameReferenceExpression.isComposable(): Boolean {
    val ktNamedFunction = resolve() as? KtNamedFunction ?: return false
    return ktNamedFunction.isComposableFunction()
}

internal fun KtNamedFunction.isComposableFunction(): Boolean {
    return CachedValuesManager.getCachedValue(this) {
        CachedValueProvider.Result.create(
            annotationEntries.any {
                COMPOSABLE_FQ_NAME == getFullyQualifiedName(it)
            },
            containingFile,
            ProjectRootModificationTracker.getInstance(project)
        )
    }
}

fun getFullyQualifiedName(annotationEntry: KtAnnotationEntry): String? {
    val calleeExpression = annotationEntry.calleeExpression
    return calleeExpression?.text
}

internal fun PsiElement.isIntentionAvailable(
    composableFunctionFinder: ComposableFunctionFinder
): Boolean {
    if (language != KotlinLanguage.INSTANCE) {
        return false
    }

    if (!isWritable) {
        return false
    }

    return parent?.let { parentPsiElement ->
        composableFunctionFinder.isFunctionComposable(parentPsiElement)
    } ?: false
}

internal const val COMPOSABLE_FQ_NAME = "androidx.compose.runtime.Composable"
