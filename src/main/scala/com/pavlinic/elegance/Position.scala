package com.pavlinic.elegance

import scalaz._, Scalaz._


case class Position(rawPos: Int, raw: String) {
  def lineNumber =
    countNewlines(raw.substring(0, rawPos)) + 1


  private def countNewlines(str: String) = str.count(_ === '\n')
}