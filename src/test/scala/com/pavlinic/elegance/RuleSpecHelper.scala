package com.pavlinic.elegance

import com.pavlinic.elegance.Checker.UnfixableRuleFailure
import com.pavlinic.elegance.Node.RichNode
import org.specs2.Specification

import scala.io.Source

import ammonite.ops._

trait RuleSpecHelper {
  this: Specification =>
  implicit val defaultRules = DefaultConfig.defaultRules

  def verifyFix(inFile: String, outFile: String, lineToError: (Int, String)*)(implicit rules: Seq[Rule]) = {
    val in = load(inFile)
    val expectedOut = load(outFile)

    val original: (RichNode, Seq[(Position, String)]) = (CodeFile(cwd / inFile, in).parse.get, Seq())

    val (finalNode, allPositions) = rules.foldLeft(original) { (last, nextRule) =>
      val (ast, positions)= last
      val (newAst, newPositions) = process(ast)(nextRule)
      (newAst, positions ++ newPositions.map((_, nextRule.message)))
    }


    Seq(
      finalNode.codeFile.rawText === expectedOut,
      allPositions.map { case (p, m) => (p.lineNumber, m) }  === lineToError
    )
  }

  private def process(ast:RichNode)(rule: Rule): (RichNode, Seq[Position]) = {
    val positions = Checker.checkRule(rule, ast) match {
      case UnfixableRuleFailure(rule, node, positions) => positions
      case _                                           => Seq()
    }

    val fixed = if (rule.fixer.isDefinedAt((ast, positions))) {
      println("Applying fixer for rule" + rule.name)
      rule.fixer((ast, positions))
    } else ast



    fixed -> positions
  }

  private def load(f: String): String = Source.fromURL(getClass.getClassLoader.getResource(f), "UTF-8").mkString
}