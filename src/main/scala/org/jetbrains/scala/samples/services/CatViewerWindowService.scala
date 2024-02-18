package org.jetbrains.scala.samples.services

import com.intellij.openapi.project.Project

class CatViewerWindowService(val project: Project) {
    val catViewerWindow = CatViewerWindow(project)
}
