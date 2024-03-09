// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.scala.samples.listeners

import com.intellij.notification.{Notification, NotificationType, Notifications}
import com.intellij.openapi.fileEditor.{FileEditorManager, FileEditorManagerEvent, FileEditorManagerListener}
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.{ToolWindow, ToolWindowManager}
import org.jetbrains.scala.samples.SamplePluginBundle
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.{PsiDocumentManager, PsiFile}
import com.intellij.ui.components.JBLabel

class FileOpenedListener extends FileEditorManagerListener {

    val logger: Logger = Logger.getFactory.getLoggerInstance(getClass.getName)

    override def fileOpened(source: FileEditorManager, file: VirtualFile): Unit = {
        Notifications.Bus.notify(
            new Notification("My Plugin Notification",
                SamplePluginBundle.message("file.opened"),
                SamplePluginBundle.message("name.getname", file.getName),
                NotificationType.INFORMATION)
        )
        logger.info(s"Files: [${source.getOpenFiles.mkString(", ")}]")
    }

    override def selectionChanged(event: FileEditorManagerEvent): Unit = {
//        val oldFileName = Option(event.getOldFile).map(_.getName).getOrElse("<none>")
//        val newFileName = Option(event.getNewFile).map(_.getName).getOrElse("<none>")
//        logger.info(s"Selection changed, oldFile=$oldFileName, newFile=$newFileName")
    }
}
