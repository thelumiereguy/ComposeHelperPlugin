package com.thelumiereguy.composeplugin.line_marker_provider;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinLanguage;

public class ComposeLineMarkerProvider implements LineMarkerProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo<?> lineMarkerInfo;

        if (!element.isWritable() || element == null) {
            return null;
        }

        if (!element.getLanguage().getID().equals(KotlinLanguage.INSTANCE.getID())) { //Compose is for Kotlin
            return null;
        }

        lineMarkerInfo = PsiElementHelperKt.getLine(element);

        return lineMarkerInfo;
    }
}
