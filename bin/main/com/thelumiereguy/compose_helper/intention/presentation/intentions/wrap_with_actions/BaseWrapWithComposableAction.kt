package com.thelumiereguy.compose_helper.intention.presentation.intentions.wrap_with_actions

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.template.impl.InvokeTemplateAction
import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.thelumiereguy.compose_helper.intention.data.composable_finder.ComposableFunctionFinder
import com.thelumiereguy.compose_helper.intention.data.composable_finder.DeepComposableFunctionFinderImpl
import com.thelumiereguy.compose_helper.intention.data.get_root_element.GetRootElement
import org.jetbrains.kotlin.idea.KotlinLanguage

abstract class BaseWrapWithComposableAction : PsiElementBaseIntentionAction(), HighPriorityAction {

    private val composableFunctionFinder: ComposableFunctionFinder by lazy {
        DeepComposableFunctionFinderImpl()
    }

    private val getRootElement by lazy {
        GetRootElement()
    }

    override fun getFamilyName(): String {
        return "Compose helper actions"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.language.id != KotlinLanguage.INSTANCE.id) { //Compose is for Kotlin
            return false
        }

        if (!element.isWritable) {
            return false
        }

        return element.parent?.let { parentPsiElement ->
            composableFunctionFinder.isFunctionComposable(parentPsiElement)
        } ?: false
    }

    override fun startInWriteAction(): Boolean = true

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        getRootElement(element.parent)?.let { rootElement ->
            val selectionModel = editor!!.selectionModel
            val textRange = rootElement.textRange
            selectionModel.setSelection(textRange.startOffset, textRange.endOffset)

            InvokeTemplateAction(
                getTemplate(),
                editor,
                project,
                HashSet()
            ).perform()
        }
    }

    protected abstract fun getTemplate(): TemplateImpl?

}
