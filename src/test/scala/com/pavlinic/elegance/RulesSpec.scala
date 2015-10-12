package com.pavlinic.elegance

import org.specs2.Specification

object RulesSpec extends Specification with RuleSpecHelper {
  def is =
    s2"""
        Rules
          Can fix tabs $tabs
      """


  def tabs = verifyFix("tabs.scala.in", "tabs.scala.out", 2 -> "Tabs are not allowed")


}
