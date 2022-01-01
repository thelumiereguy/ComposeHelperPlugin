package com.thelumiereguy.compose_helper.intention.presentation.intentions

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.thelumiereguy.compose_helper.intention.data.composable_finder.ComposableFunctionFinder
import com.thelumiereguy.compose_helper.intention.data.composable_finder.DeepComposableFunctionFinderImpl
import com.thelumiereguy.compose_helper.intention.data.composable_wrapper.ComposableWrapper
import com.thelumiereguy.compose_helper.intention.data.composable_wrapper.ProvidesComposableTemplate
import com.thelumiereguy.compose_helper.intention.data.get_root_element.GetRootElement
import com.thelumiereguy.compose_helper.intention.presentation.icons.SdkIcons
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

class WrapWithCardIntention : PsiElementBaseIntentionAction(), ProvidesComposableTemplate, Iconable,
    HighPriorityAction {

    override fun getText(): String {
        return "Wrap with Card"
    }

    override fun getFamilyName(): String {
        return "ComposableIntention"
    }

    private val composableFunctionFinder: ComposableFunctionFinder = DeepComposableFunctionFinderImpl()

    private val getRootElement = GetRootElement()

    private val composableWrapper = ComposableWrapper(this)

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

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        getRootElement(element.parent)?.let { rootElement ->
            composableWrapper.wrap(rootElement, project)
        }
    }

    override val composableTemplatePrefix = "Card(modifier = Modifier) {"

    override val composableTemplateSuffix = "}"

    override fun getIcon(flags: Int): Icon = SdkIcons.composeIcon
}
