package com.pavlinic.elegance

import scalariform.parser.CompilationUnit
import Node._

object Rules {
  def fileLength(len: Int) = new Rule {
    def name: String = "file-length"
    def message: String = s"File length exceeds $len lines"

    def matcher: NodeMatcher = {
      case RichNode(CompilationUnit(_, _), _) => true
    }


    def checker: NodeChecker = {
      case RichNode(CompilationUnit(_, _), CodeFile(_, raw)) =>
        if (raw.lines.length < len) Seq() else Seq(Position(0, 1))
    }

    def fixer: NodeFixer = ???
  }

  def lineLength(len: Int) = new Rule {
    def name: String = "line-length"
    def message: String = s"Line length exceeds $len characters"

    def matcher: NodeMatcher = {
      case RichNode(CompilationUnit(_, _), _) => true
    }

    def checker: NodeChecker = {
      case RichNode(CompilationUnit(_, _), CodeFile(_, raw)) => {
        val lines = raw.lines.toSeq
        lines.filter(_.length > len).map {line =>
          // bug here for dup lines
          Position(raw.indexOf(line), lines.indexOf(line))
        }
      }
    }

    def fixer: NodeFixer = ???
  }


}
