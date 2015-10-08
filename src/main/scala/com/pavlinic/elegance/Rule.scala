package com.pavlinic.elegance

import com.pavlinic.elegance.Node.RichNode


trait Rule {
  type NodeMatcher = PartialFunction[RichNode, Boolean]
  type NodeChecker = PartialFunction[RichNode, Seq[Position]]
  type NodeFixer = RichNode => RichNode

  def name: String
  def message: String
  def matcher: NodeMatcher
  def checker: NodeChecker
  def fixer: NodeFixer

}
case class Position(rawPos: Int, lineNumber: Int)
