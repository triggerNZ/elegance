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

import com.pavlinic.elegance.Checker.{FixableRuleFailure, UnfixableRuleFailure}
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
      case FixableRuleFailure(rule, node, positions)   => positions
      case _                                           => Seq()
    }


    val fixed = if (rule.fixer.isDefinedAt((ast, positions))) {
      rule.fixer((ast, positions)).getOrElse(throw new RuntimeException("Could not parse fixed tree"))
    } else ast



    fixed -> positions
  }

  private def load(f: String): String = Source.fromURL(getClass.getClassLoader.getResource(f), "UTF-8").mkString
}