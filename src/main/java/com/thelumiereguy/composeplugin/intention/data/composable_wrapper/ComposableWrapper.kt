package com.thelumiereguy.composeplugin.intention.data.composable_wrapper

import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.psi.KtPsiFactory

class ComposableWrapper(private val composableTemplateProvider: ProvidesComposableTemplate) {

    fun wrap(
        rootElement: PsiElement,
        project: Project,
        postProcessingBlock: (PsiElement) -> Unit = { CodeStyleManager.getInstance(project).reformat(it) }
    ) {
        CommandProcessor.getInstance().executeCommand(
            project, {
                val newElement = KtPsiFactory(project).createExpression(
                    composableTemplateProvider.composableTemplatePrefix +
                            rootElement.text +
                            composableTemplateProvider.composableTemplateSuffix
                )
                rootElement.replace(newElement)

                postProcessingBlock(newElement)
            }, "",
            "composeplugin"
        )
    }
}
