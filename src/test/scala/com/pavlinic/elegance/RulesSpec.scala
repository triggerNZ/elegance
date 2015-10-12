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

import org.specs2.Specification

object RulesSpec extends Specification with RuleSpecHelper {
  def is =
    s2"""
        Rules
          Can fix tabs $tabs
      """


  def tabs = verifyFix("tabs.scala.in", "tabs.scala.out", 2 -> "Tabs are not allowed")


}
