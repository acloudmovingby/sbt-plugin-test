// Copyright 2000-2024 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.scala.samples.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.ui.components.JBLabel
import org.jetbrains.scala.samples.SamplePluginBundle

class ChrisToolWindowFactory extends ToolWindowFactory {

  override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit =
    toolWindow.getComponent.add(new JBLabel(SamplePluginBundle.message("chris.tool.window")))

}
