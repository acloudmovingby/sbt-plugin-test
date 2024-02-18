package org.jetbrains.scala.samples.listeners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.{CaretEvent, CaretListener}

class CaretChangeListener extends CaretListener {

    implicit val logger: Logger = Logger.getFactory.getLoggerInstance(getClass.getName)

    override def caretPositionChanged(event: CaretEvent): Unit = {
        logger.info(s"dragon - caretPositionChanged: ${event.getNewPosition}")
    }

    override def caretAdded(event: CaretEvent): Unit = {
        logger.info(s"dragon - caretAdded: ${event.getNewPosition}")
    }

    override def caretRemoved(event: CaretEvent): Unit = {
        logger.info(s"dragon - caretRemoved: ${event.getNewPosition}")
    }
}
