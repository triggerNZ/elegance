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

import scala.meta._
import ammonite.ops._

import org.specs2.Specification

import scala.meta.internal.ast.{Pkg}

object NodeSpec extends Specification {
  def is = s2"""
            Nodes can:
              Produce an in-order traversal $inOrder
           """

  def inOrder = {
    val scalaFile =
      """ package pkg
        |
        | object Main {
        |   def main(args: Array[String]) = {
        |     println("Hello World")
        |   }
        | }
      """.stripMargin

    val node = CodeFile(cwd / "hello.scala", scalaFile).parse.get
    val preorder = node.preorder.map(_.n)
    Seq(
      preorder(0) must beAnInstanceOf[Source],
      preorder(1) must beAnInstanceOf[Pkg], // Package. What the hell is a member term???
      preorder(2) must beAnInstanceOf[Term.Name],  // pkg. What the hell is a member term???
      preorder(3) must beAnInstanceOf[Member.Term],
      preorder(4) must beAnInstanceOf[Term.Name]


    )
  }
}
