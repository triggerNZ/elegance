package com.pavlinic.elegance

import scalariform.parser.AstNode
import scalaz._, Scalaz._

object Node {
  case class RichNode(n: AstNode, codeFile: CodeFile) {
    def file = codeFile.path.toString

    def replaceStringAt(position: Int, originalLength: Int, newText: String): Option[RichNode] = {
      //mutable but localised
      val builder = StringBuilder.newBuilder
      builder.append(codeFile.rawText)
      builder.delete(position, position + originalLength)
      builder.insert(position, newText)
      codeFile.copy(rawText = builder.mkString).parse
    }
  }


  implicit def toRichNode(n: AstNode)(implicit codeFile: CodeFile) = {
    RichNode(n, codeFile)
  }
}
