package com.pavlinic.elegance

import ammonite.ops.Path
import com.pavlinic.elegance.Node.RichNode

import scalariform.ScalaVersions
import scalariform.parser.ScalaParser

case class CodeFile(path: Path, rawText: String) {
  def parse: Option[RichNode] = {
    ScalaParser.parse(this.rawText, ScalaVersions.Scala_2_11.toString).map( n => RichNode(n, this))
  }
}
