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

/**
 * Created by tin on 9/10/15.
 */
package object util {
  implicit class ConsoleColorise(val str: String) extends AnyVal {
    import Console._

    def black     = s"$BLACK$str"
    def red       = s"$RED$str"
    def green     = s"$GREEN$str"
    def yellow    = s"$YELLOW$str"
    def blue      = s"$BLUE$str"
    def magenta   = s"$MAGENTA$str"
    def cyan      = s"$CYAN$str"
    def white     = s"$WHITE$str"

    def blackBg   = s"$BLACK_B$str"
    def redBg     = s"$RED_B$str"
    def greenBg   = s"$GREEN_B$str"
    def yellowBg  = s"$YELLOW_B$str"
    def blueBg    = s"$BLUE_B$str"
    def magentaBg = s"$MAGENTA_B$str"
    def cyanBg    = s"$CYAN_B$str"
    def whiteBg   = s"$WHITE_B$str"
  }
}
