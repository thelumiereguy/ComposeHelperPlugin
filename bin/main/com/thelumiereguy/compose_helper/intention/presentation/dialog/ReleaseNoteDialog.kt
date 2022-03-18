package com.thelumiereguy.compose_helper.intention.presentation.dialog

import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JTextArea
import javax.swing.JPanel
import javax.swing.JComponent

class ReleaseNoteDialog : DialogWrapper(), Action {
    private val txtReleaseNote: JTextArea? = null
    private val panelWrapper: JPanel? = null

    override fun createCenterPanel(): JComponent? {
        return panelWrapper
    }

    override fun getOKAction(): Action {
        return this
    }

    override fun actionPerformed(e: ActionEvent?) {
        super.doOKAction()
    }
}
