package org.jetbrains.scala.samples.listeners

import scala.jdk.CollectionConverters._
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.{EditorMouseEvent, EditorMouseListener}
import com.intellij.psi.{PsiDocumentManager, PsiElement}

class CaretChangeListener extends EditorMouseListener {

    implicit val logger: Logger = Logger.getFactory.getLoggerInstance(getClass.getName)

    def containingFileOffset(psiElem: PsiElement): String = Option(psiElem.getContainingFile).map { file =>
        s"${file.getName}:${psiElem.getTextOffset.toString}"
    }.getOrElse("unknown file")

    def displayPSIElementInfo(psiElem: Option[PsiElement], name: String): Unit = {
        logger.info(s"\t$name: ${psiElem.map(_.getText).getOrElse("None")} (${psiElem.map(_.getClass.getName).getOrElse("None")}) at ${psiElem.map(containingFileOffset).getOrElse("")}")
        logger.info(s"\t\ttext: ${psiElem.map(_.getText).getOrElse("-")}")
        val ref = psiElem.flatMap(p => Option(p.getReference))
        logger.info(s"\t\treference getElement: ${ref.map(_.getElement).getOrElse("-")}")
        logger.info(s"\t\treference getCanonicalText: ${ref.map(_.getCanonicalText).getOrElse("-")}")
        val resolvedRef = ref.flatMap(r => Option(r.resolve()))
        logger.info(s"\t\treference resolve: ${resolvedRef.getOrElse("-")}")
        logger.info(s"\t\treference resolve offset: ${resolvedRef.map(_.getTextOffset.toString).getOrElse("-")}")
        logger.info(s"\t\treference resolve text: ${resolvedRef.map(_.getText).getOrElse("-")}")
    }

    def logFamilyOfElement(psiElement: PsiElement) = {
        val parent = Option(psiElement.getParent)
        val grandparent = parent.flatMap(p => Option(p.getParent))
        val firstChild = Option(psiElement.getFirstChild)

        displayPSIElementInfo(grandparent, "GRANDPARENT")
        displayPSIElementInfo(parent, "PARENT")
        displayPSIElementInfo(Option(psiElement), "ELEMENT")
        displayPSIElementInfo(firstChild, "FIRSTCHILD")
    }

    def logAllChildrenTypes(psiElement: PsiElement): Unit = {
        def logChildrenTypes(psiElement: PsiElement, tabs: String): Unit = {
            val children = psiElement.getChildren
            children.foreach { child =>
                logger.info(s"\tchild (${child.getClass.getName}):  ${child.getText.replace("\n"," ")} ")
                logChildrenTypes(child, s"$tabs\t")
            }
        }
        logChildrenTypes(psiElement, "")
    }

    def parentsReferenceResolved(psiElement: PsiElement): Option[PsiElement] = Option(psiElement.getParent)
        .flatMap(p => Option(p.getReference))
        .flatMap(r => Option(r.resolve()))

    override def mouseClicked(event: EditorMouseEvent): Unit = {
        for {
            editor <- Option(event.getEditor)
            project <- Option(editor.getProject)
            document = editor.getDocument
            caretModel = editor.getCaretModel
            offset = caretModel.getOffset
            documentManager <- Option(PsiDocumentManager.getInstance(project))
            file <- Option(documentManager.getPsiFile(document))
            fileViewProvider = file.getViewProvider
            languages = fileViewProvider.getLanguages.asScala
            hasScala = languages.exists(l => l.isKindOf("Scala"))
            psiElement <- Option(file.findElementAt(offset))
        } yield {
            logger.info(s"***CURSOR CLICK***")
            logger.info(s"${file.getName}: isScala? $hasScala")


            // this is useful for seeing the grandparent/parent/child of the element I clicked on and seeing which have references
            logFamilyOfElement(psiElement)

            // The parent of where you click actually has the reference, so this code follows the parent's reference and displays its children
            // in other words it shows the elements in the function definition of the method you clicked on
//            logger.info(s"Children of parent's reference:")
//            parentsReferenceResolved(psiElement).foreach { parent =>
//                logger.info(s"${parent.getText}:")
//                logAllChildrenTypes(parent)
//            }
        }
    }

}
