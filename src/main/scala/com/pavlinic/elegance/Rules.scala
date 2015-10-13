//   Copyright 2015 Tin Pavlinic
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
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
      case RichNode(CompilationUnit(_, _), CodeFile(_, raw)) => {
        raw.zipWithIndex.filter(_._1 === '\t').map { case (tab, pos) => Position(pos, raw) }
      }
    }

    def fixer: NodeFixer = {
      case (node, positions) => {
        Some(positions.foldLeft(node) { (n, pos) =>
          //get should be safe here because tab to space replacement does not affect compiationallPositions
          n.replaceStringAt(pos.rawPos, 1, " " * spacesPerTab).get
        })
      }
    }
  }

  def headerMatches(header: String) = new Rule {
    def name: String = "header-matches"
    def message: String = s"Incorrect header"

    def matcher: NodeMatcher = {
      case RichNode(CompilationUnit(_, _), _) => true
    }

    def checker: NodeChecker = {
      case RichNode(CompilationUnit(_, _), CodeFile(_, raw)) => {
        if (raw.startsWith(header)) {
          Seq()
        } else {
          Seq(Position(0, raw))
        }
      }
    }

    def fixer: NodeFixer = { case (node, positions) =>
      node.replaceStringAt(0, 0, header)
    }
  }
}
