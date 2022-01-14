package com.thelumiereguy.compose_helper.intention.presentation.intentions.wrap_with_actions

import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.codeInsight.template.impl.TemplateSettings

class WrapWithLzyColumnIntention : BaseWrapWithComposableAction() {

    override fun getText(): String {
        return "Wrap with Lazy Column"
    }

    override fun getTemplate(): TemplateImpl? {
        return TemplateSettings.getInstance().getTemplate("WwLazyCol", "WrappedComposables")
    }
}