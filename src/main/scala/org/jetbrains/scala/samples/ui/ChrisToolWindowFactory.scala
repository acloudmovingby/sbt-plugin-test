// Copyright 2000-2024 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.scala.samples.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.{ComponentManager, ServiceManager}
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.ui.components.JBLabel
import org.jetbrains.scala.samples.SamplePluginBundle
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.CaretListener
import org.jetbrains.scala.samples.listeners.CaretChangeListener
import org.jetbrains.scala.samples.services.CatViewerWindowService

class ChrisToolWindowFactory extends ToolWindowFactory {

    val logger: Logger = Logger.getFactory.getLoggerInstance(getClass.getName)

    override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = {

        val catViewerWindow = project.getService(classOf[CatViewerWindowService]).catViewerWindow
        //val catViewerWindow = ApplicationManager.getApplication().getService(classOf[CatViewerWindowService]).catViewerWindow
        val _catViewerWindow = ServiceManager.getService(project, classOf[CatViewerWindowService]).catViewerWindow
        val component = toolWindow.getComponent
        component.getParent.add(catViewerWindow.content)

        val theText = for {
            proj <- Option(project)
            instance <- Option(FileEditorManager.getInstance(proj))
            editor <- Option(instance.getSelectedTextEditor())
            doc = editor.getDocument()
            txt = doc.getText()
        } yield txt

        toolWindow.getComponent.add(new JBLabel(s"Hey ${theText.getOrElse("<empty>")}"))
        toolWindow.getContentManagerIfCreated.addContent(
            toolWindow.getContentManagerIfCreated.getFactory.createContent(new JBLabel("Jude"), "It's me", false)
        )

        logger.info("This is a message!!")

        for {
            proj <- Option(project)
            instance <- Option(FileEditorManager.getInstance(proj))
            editor <- Option(instance.getSelectedTextEditor())
            caretModel = editor.getCaretModel()
        } yield {
            logger.info("Making new caret listener")
            caretModel.addCaretListener(new CaretChangeListener())
        }
    }

}
