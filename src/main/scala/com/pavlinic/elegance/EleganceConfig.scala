package com.pavlinic.elegance

import Rules._

trait EleganceConfig {
  def description: String = "Default elegance config"

  def rules: List[Rule]

  def defaultRules: List[Rule] = List(
    fileLength(100),
    lineLength(80),
    noTabs(2)
  )
}

object DefaultConfig extends EleganceConfig {
  def rules = defaultRules
}
