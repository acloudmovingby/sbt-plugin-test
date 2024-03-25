package org.acloudmovingby.graphit.services

import com.intellij.openapi.project.Project

class CatViewerWindowService(val project: Project) {
    val catViewerWindow = CatViewerWindow(project)
}
