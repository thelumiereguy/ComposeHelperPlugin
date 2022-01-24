package com.thelumiereguy.compose_helper.intention.presentation.intentions.wrap_with_actions

import com.intellij.codeInsight.completion.actions.CodeCompletionAction
import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class WrapWithYourOwnIntention : BaseWrapWithComposableAction() {

    override fun getText(): String {
        return "Wrap with your own Composable"
    }

    override fun getTemplate(): TemplateImpl? {
        return TemplateSettings.getInstance().getTemplate("WwComposable", "ComposeHelperTemplate")
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        super.invoke(project, editor, element)
    }
}
