package com.pavlinic.elegance

import scalariform.parser.AstNode
import scalaz._, Scalaz._

object Node {
  case class RichNode(n: AstNode, codeFile: CodeFile) {
    def file = codeFile.path.toString
  }


  implicit def toRichNode(n: AstNode)(implicit codeFile: CodeFile) = {
    RichNode(n, codeFile)
  }
}
