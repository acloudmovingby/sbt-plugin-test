package org.jetbrains.scala.samples.listeners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.{CaretEvent, CaretListener}

class CaretChangeListener extends CaretListener {

    implicit val logger: Logger = Logger.getFactory.getLoggerInstance(getClass.getName)

    override def caretPositionChanged(event: CaretEvent): Unit = {
        logger.info(s"caretPositionChanged: ${event.getNewPosition}")
    }
}
