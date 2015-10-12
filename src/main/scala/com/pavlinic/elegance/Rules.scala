package com.pavlinic.elegance

import scalariform.parser.CompilationUnit
import Node._
import scalaz._, Scalaz._

object Rules {
  def fileLength(len: Int) = new Rule {
    def name: String = "file-length"
    def message: String = s"File length exceeds $len lines"

    def matcher: NodeMatcher = {
      case RichNode(CompilationUnit(_, _), _) => true
    }


    def checker: NodeChecker = {
      case RichNode(CompilationUnit(_, _), CodeFile(_, raw)) =>
        if (raw.lines.length < len) Seq() else Seq(Position(0, raw))
    }

    def fixer: NodeFixer = Map.empty
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
          Position(raw.indexOf(line), raw)
        }
      }
    }

    def fixer: NodeFixer = Map.empty
  }

  def noTabs(spacesPerTab : Int = 2) = new Rule {
    def name: String = "no-tabs"
    def message: String = s"Tabs are not allowed"

    def matcher: NodeMatcher = {
      case RichNode(CompilationUnit(_, _), _) => true
    }


    def checker: NodeChecker = {
      case RichNode(CompilationUnit(_, _), CodeFile(_, raw)) =>
        raw.zipWithIndex.filter(_._1 === '\t').map { case (tab, pos) => Position(pos, raw) }
    }

    def fixer: NodeFixer = { case (node, positions) =>
      positions.foldLeft(node) { (n, pos) =>
        //get should be safe here because tab to space replacement does not affect compiationallPositions
        n.replaceStringAt(pos.rawPos, 1, " " * spacesPerTab).get
      }
    }
  }


}
