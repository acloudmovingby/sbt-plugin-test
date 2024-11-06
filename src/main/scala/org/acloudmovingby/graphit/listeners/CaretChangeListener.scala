package org.acloudmovingby.graphit.listeners

import com.intellij.lang.Language
import org.jetbrains.plugins.scala.lang.psi.api.{ScalaFile, ScalaPsiElement, ScalaRecursiveElementVisitor}

import scala.jdk.CollectionConverters._
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.{EditorMouseEvent, EditorMouseListener}
import com.intellij.psi.{PsiDocumentManager, PsiElement, PsiFile}
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression

class CaretChangeListener extends EditorMouseListener {

    private var visitCounter = 0

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

    def getScalaPsiFile(file: PsiFile): Option[ScalaFile] = for {
        fileViewProvider <- Option(file.getViewProvider)
        languages = fileViewProvider.getLanguages.asScala
        hasScala = languages.exists(l => l.isKindOf("Scala"))
        scalaLanguage <- Option(Language.findLanguageByID("Scala"))
        psiFile <- Option(fileViewProvider.getPsi(scalaLanguage))
        scalaPsiFile <- psiFile match {
            case scalaFile: ScalaFile => logger.info(s"File is a Scala file!")
                Some(scalaFile)
            case _ => logger.info(s"File is not a Scala file, but instead is ${psiFile.getClass.getName}")
                None
        }
    } yield scalaPsiFile

    override def mouseClicked(event: EditorMouseEvent): Unit = {
        for {
            editor <- Option(event.getEditor)
            project <- Option(editor.getProject)
            document = editor.getDocument
            caretModel = editor.getCaretModel
            offset = caretModel.getOffset
            documentManager <- Option(PsiDocumentManager.getInstance(project))
            file <- Option(documentManager.getPsiFile(document))
            psiElement <- Option(file.findElementAt(offset))
        } yield {
            logger.info(s"***CURSOR CLICK***")
            visitCounter = 0

            val psiTreeForScala = getScalaPsiFile(file)

            file.accept(new ScalaRecursiveElementVisitor() {
                override def visitReferenceExpression(ref: ScReferenceExpression): Unit = for {
                    _ <- Option(super.visitReferenceExpression(ref))
                    elem <- Option(ref.resolve())
                    _ = {
                        logger.info(
                            s"Reference expression text: ${ref.getText} (elem class: ${elem.getClass.getSimpleName}) at " +
                                s"${Option(elem.getContainingFile).map(_.getName).getOrElse("")}:${elem.getTextOffset.toString}")
                        // don't do this I think, it causes some repetition of visits, but I didn't explore it much
                        //super.visitReferenceExpression(ref)
                    }
                } yield {
                    visitCounter += 1
                    logger.info(s"visitCounter = $visitCounter")
                }

                /** So this one appears to just return the whole element of the file and the counter only goes to 1 (it doesn't recur). I don't get it */
//                override def visitScalaElement(element: ScalaPsiElement): Unit = {
//                        logger.info(
//                            s"Reference expression text: ${element.getText} (elem class: ${element.getClass.getSimpleName}) at " +
//                                s"${Option(element.getContainingFile).map(_.getName).getOrElse("")}:${element.getTextOffset.toString}")
//                        //super.visitReferenceExpression(ref)
//                    visitCounter += 1
//                    logger.info(s"visitCounter = $visitCounter")
//                }
            });

            /** I think this finds the top level object/class thing in a file?? I don't remember */
//            psiTreeForScala.map { scalaPsiFile =>
//                scalaPsiFile.typeDefinitions.foreach { typeDef =>
//                    logger.info(s"Type definition: ${typeDef.getText}")
//                }
//            }

//            logger.info(s"Class of ScalaPsiFile: ${psiTreeForScala.foreach(_.getClass.getName)}")

            // this is useful for seeing the grandparent/parent/child of the element I clicked on and seeing which have references
            /*
            logFamilyOfElement(psiElement)
            */

            // The parent of where you click actually has the reference, so this code follows the parent's reference and displays its children
            // in other words it shows the elements in the function definition of the method you clicked on
            /*
            logger.info(s"Children of parent's reference:")
            parentsReferenceResolved(psiElement).foreach { parent =>
                logger.info(s"${parent.getText}:")
                logAllChildrenTypes(parent)
            }
            */
        }
    }

}
