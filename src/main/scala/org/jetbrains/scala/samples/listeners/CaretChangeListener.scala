package org.jetbrains.scala.samples.listeners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.{EditorMouseEvent, EditorMouseListener}
import com.intellij.psi.PsiDocumentManager

class CaretChangeListener extends EditorMouseListener {

    implicit val logger: Logger = Logger.getFactory.getLoggerInstance(getClass.getName)

    override def mouseClicked(event: EditorMouseEvent): Unit = {
        logger.info("mouse clicked")
        for {
            editor <- Option(event.getEditor)
            project <- Option(editor.getProject)
            document = editor.getDocument
            caretModel = editor.getCaretModel
            offset = caretModel.getOffset
            psiElement <- Option(PsiDocumentManager.getInstance(project).getPsiFile(document).findElementAt(offset))
        } yield {
            logger.info(s"psiElement at cursor=${psiElement.getText}")
        }
    }

}
