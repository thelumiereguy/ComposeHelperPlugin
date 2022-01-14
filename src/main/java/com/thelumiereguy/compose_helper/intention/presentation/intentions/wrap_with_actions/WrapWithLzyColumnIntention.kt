package com.thelumiereguy.compose_helper.intention.presentation.intentions.wrap_with_actions

import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.codeInsight.template.impl.TemplateSettings

//class WrapWithLzyColumnIntention : PsiElementBaseIntentionAction(), ProvidesComposableTemplate, Iconable,
//    HighPriorityAction {
//
//    override fun getText(): String {
//        return "Wrap with Lazy Column"
//    }
//
//    override fun getFamilyName(): String {
//        return "ComposableIntention"
//    }
//
//    private val composableFunctionFinder: ComposableFunctionFinder = DeepComposableFunctionFinderImpl()
//
//    private val getRootElement = GetRootElement()
//
//    private val composableWrapper = ComposableWrapper(this)
//
//    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
//        if (element.language.id != KotlinLanguage.INSTANCE.id) { //Compose is for Kotlin
//            return false
//        }
//
//        if (!element.isWritable) {
//            return false
//        }
//
//        return element.parent?.let { parentPsiElement ->
//            composableFunctionFinder.isFunctionComposable(parentPsiElement)
//        } ?: false
//    }
//
//    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
//        getRootElement(element.parent)?.let { rootElement ->
//            composableWrapper.wrap(rootElement, project)
//        }
//    }
//
//    override val composableTemplatePrefix = """LazyColumn(modifier = Modifier) {
//        item {
//    """.trimMargin()
//
//    override val composableTemplateSuffix = "}}"
//
//    override fun getIcon(flags: Int): Icon = SdkIcons.composeIcon
//}

class WrapWithLzyColumnIntention : BaseWrapWithComposableAction() {

    override fun getText(): String {
        return "Wrap with Lazy Column"
    }

    override fun getTemplate(): TemplateImpl? {
        return TemplateSettings.getInstance().getTemplate("WwLazyCol", "WrappedComposables")
    }
}
