package org.acloudmovingby.graphit.services

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.ui.jcef.JBCefBrowser
import javax.swing.JComponent
import org.cef.CefApp

case class CatViewerWindow(project: Project) {
    private lazy val webView: JBCefBrowser = {
        // TODO add error handling / logging here if JBCefBrowser isn't supported, see: https://plugins.jetbrains.com/docs/intellij/jcef.html#using-jcef-in-a-plugin
        val browser = new JBCefBrowser()
        registerAppSchemeHandler()
        browser.loadURL("http://myapp/index.html")
        Disposer.register(project, browser)
        browser
    }

    def content: JComponent = webView.getComponent

    private def registerAppSchemeHandler(): Unit = {
        CefApp
            .getInstance()
            .registerSchemeHandlerFactory(
                "http",
                "myapp",
                new CustomSchemeHandlerFactory
            )
    }

    private def setupJSCallback(): Unit = {
        // JBCefJSQuery
    }
}
