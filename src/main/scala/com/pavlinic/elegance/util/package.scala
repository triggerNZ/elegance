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
