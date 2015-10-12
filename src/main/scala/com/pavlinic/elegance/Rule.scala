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


trait Rule {
  type NodeMatcher = PartialFunction[RichNode, Boolean]
  type NodeChecker = PartialFunction[RichNode, Seq[Position]]
  type NodeFixer = PartialFunction[(RichNode, Seq[Position]), Option[RichNode]]

  def name: String
  def message: String
  def matcher: NodeMatcher
  def checker: NodeChecker
  def fixer: NodeFixer

}

