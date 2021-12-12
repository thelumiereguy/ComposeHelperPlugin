package com.thelumiereguy.composeplugin.intention.presentation.intentions

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.thelumiereguy.composeplugin.intention.data.composable_finder.ComposableFunctionFinder
import com.thelumiereguy.composeplugin.intention.data.composable_finder.DeepComposableFunctionFinderImpl
import com.thelumiereguy.composeplugin.intention.data.composable_wrapper.ComposableWrapper
import com.thelumiereguy.composeplugin.intention.data.composable_wrapper.ProvidesComposableTemplate
import com.thelumiereguy.composeplugin.intention.data.get_root_element.GetRootElement
import com.thelumiereguy.composeplugin.intention.presentation.icons.SdkIcons
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.core.deleteSingle
import javax.swing.Icon

class RemoveComposableIntention : PsiElementBaseIntentionAction(), ProvidesComposableTemplate, Iconable,
    HighPriorityAction {

    override fun getText(): String {
        return "Remove this Composable"
    }

    override fun getFamilyName(): String {
        return "ComposableIntention"
    }

    private val composableFunctionFinder: ComposableFunctionFinder = DeepComposableFunctionFinderImpl()

    private val getRootElement = GetRootElement()

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element == null) {
            return false
        }

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
        getRootElement(element.parent)?.delete()
    }

    override val composableTemplatePrefix = "Row(modifier = Modifier) {"
    override val composableTemplateSuffix = "}"

    override fun getIcon(flags: Int): Icon = SdkIcons.composeIcon

}
