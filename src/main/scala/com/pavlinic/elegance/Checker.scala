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

import com.pavlinic.elegance.Node.RichNode

import scalariform._
import scalariform.parser.AstNode

object Checker {
  def check(ast: RichNode)(implicit rules: Seq[Rule]) = {
    rules.map { rule =>
      checkRule(rule, ast)
    }
  }

  def fixNode(ast: RichNode)(implicit rules: Seq[Rule]): RichNode = {
    rules.foldLeft(ast) { (curAst, nextRule) =>
     if (nextRule.matcher.isDefinedAt(curAst) && nextRule.checker.isDefinedAt(curAst)) {
      val positions = nextRule.checker(ast)
      if (nextRule.fixer.isDefinedAt(curAst, positions)) {
        nextRule.fixer((curAst, positions)).get //crashing here is a concious decision, because it indicates the rule is broken
      } else ast
     } else ast

    }
  }

  def checkRule(rule: Rule, astNode: RichNode) : Result = {
    if (!rule.matcher.isDefinedAt(astNode) || !rule.matcher(astNode)) {
      NA
    } else {
      val errorPositions = rule.checker(astNode)
      if (errorPositions.isEmpty) {
        Ok
      } else if (rule.fixer.isDefinedAt((astNode, errorPositions))){
        FixableRuleFailure(rule, astNode, errorPositions)
      } else {
        UnfixableRuleFailure(rule, astNode, errorPositions)
      }
    }
  }


  sealed trait Result
  case object NA extends Result
  case object Ok extends Result
  case class UnfixableRuleFailure(rule: Rule, astNode: RichNode, errorPositions: Seq[Position]) extends Result
  case class FixableRuleFailure(rule: Rule, astNode: RichNode, errorPositions: Seq[Position]) extends Result

  implicit class ResultMethods(result: Result) {
    def isFailure = result match {
      case UnfixableRuleFailure(_, _, _) => true
      case FixableRuleFailure(_, _, _) => true
      case _ => false
    }
  }
}
