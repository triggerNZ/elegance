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

import scalaz._, Scalaz._

import scala.meta._

object Node {
  case class RichNode(n: Source, codeFile: CodeFile) {
    def file = codeFile.path.toString

    def replaceStringAt(position: Int, originalLength: Int, newText: String): Option[RichNode] = {
      //mutable but localised
      val builder = StringBuilder.newBuilder
      builder.append(codeFile.rawText)
      builder.delete(position, position + originalLength)
      builder.insert(position, newText)
      codeFile.copy(rawText = builder.mkString).parse
    }
  }


  implicit def toRichNode(n: Source)(implicit codeFile: CodeFile) = {
    RichNode(n, codeFile)
  }
}
