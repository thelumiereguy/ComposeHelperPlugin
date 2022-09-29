package com.thelumiereguy.compose_helper.intention.presentation.intentions

import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.thelumiereguy.compose_helper.intention.data.composable_finder.ComposableFunctionFinder
import com.thelumiereguy.compose_helper.intention.data.composable_finder.DeepComposableFunctionFinderImpl
import com.thelumiereguy.compose_helper.intention.data.get_root_element.GetRootElement
import com.thelumiereguy.compose_helper.intention.presentation.dialog.ExtractInterfaceDialog
import com.thelumiereguy.compose_helper.intention.presentation.icons.SdkIcons
import javax.swing.Icon
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.base.utils.fqname.getKotlinFqName
import org.jetbrains.kotlin.idea.core.getPackage
import org.jetbrains.kotlin.idea.refactoring.introduce.extractFunction.ExtractKotlinFunctionHandler
import org.jetbrains.kotlin.idea.refactoring.showWithTransaction
import org.jetbrains.kotlin.idea.util.application.executeCommand
import org.jetbrains.kotlin.j2k.getContainingClass
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

class ExtractComposableIntention : PsiElementBaseIntentionAction(), Iconable, PriorityAction {

    override fun startInWriteAction(): Boolean = false

    override fun getText(): String {
        return "Extract composable"
    }

    override fun getFamilyName(): String {
        return "Compose helper actions"
    }

    private val getRootElement = GetRootElement()

    private val composableFunctionFinder: ComposableFunctionFinder = DeepComposableFunctionFinderImpl()

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
//        getRootElement(element.parent)?.delete()
        val rootComposable = getRootElement(element.parent) ?: return

        val sourceFile = element.containingFile

        val composableName = if (rootComposable is KtCallExpression) {
            rootComposable.getChildOfType<KtNameReferenceExpression>()?.getReferencedName()
        } else {
            rootComposable.getChildOfType<KtCallExpression>()
                ?.getChildOfType<KtNameReferenceExpression>()
                ?.getReferencedName()
        } ?: return

        val packageName = element.containingFile.containingDirectory.getPackage()?.qualifiedName ?: return

//        ExtractInterfaceDialog(
//            project,
//            rootComposable,
//            composableName,
//            sourceFile,
//            packageName
//        ).showWithTransaction()

        ExtractKotlinFunctionHandler(true, ExtractKotlinFunctionHandler.InteractiveExtractionHelper).invoke(
            project,
            editor!!,
            sourceFile,
            null
        )
    }

    override fun getIcon(flags: Int): Icon = SdkIcons.composeIcon

    override fun getPriority(): PriorityAction.Priority {
        return PriorityAction.Priority.NORMAL
    }
}
