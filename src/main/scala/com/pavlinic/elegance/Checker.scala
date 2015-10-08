package com.pavlinic.elegance

import com.pavlinic.elegance.Node.RichNode

import scalariform._
import scalariform.parser.AstNode

object Checker {
  def check(ast: RichNode)(implicit config: EleganceConfig) = {
    config.rules.map { rule =>
      checkRule(rule, ast)
    }
  }
  def checkRule(rule: Rule, astNode: RichNode) : Result = {
    if (!rule.matcher.isDefinedAt(astNode) || !rule.matcher(astNode)) {
      NA
    } else {
      val errorPositions = rule.checker(astNode)
      if (errorPositions.isEmpty) {
        Ok
      } else {
        UnfixableRuleFailure(rule, astNode, errorPositions)
      }
    }
  }


  sealed trait Result
  case object NA extends Result
  case object Ok extends Result
  case class UnfixableRuleFailure(rule: Rule, astNode: RichNode, errorPositions: Seq[Position]) extends Result
  case class FixableRuleFailure(rule: Rule, astNode: RichNode) extends Result

  implicit class ResultMethods(result: Result) {
    def isFailure = result match {
      case UnfixableRuleFailure(_, _, _) => true
      case FixableRuleFailure(_, _) => true
      case _ => false
    }
  }
}
