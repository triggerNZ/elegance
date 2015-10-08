package com.pavlinic.elegance

import scalariform.parser.AstNode
import scalaz._, Scalaz._

object Node {
  case class RichNode(n: AstNode, codeFile: CodeFile) {
    def file = codeFile.path.toString

    def lineNumber =
      countNewlines(codeFile.rawText.substring(0, n.firstToken.offset)) + 1


    private def countNewlines(str: String) = str.count(_ === '\n')
  }


  implicit def toRichNode(n: AstNode)(implicit codeFile: CodeFile) = {
    RichNode(n, codeFile)
  }
}
