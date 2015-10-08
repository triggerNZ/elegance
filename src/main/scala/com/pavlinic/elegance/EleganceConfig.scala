package com.pavlinic.elegance

trait EleganceConfig {
  def description: String = "Default elegance config"

  def rules: List[Rule]
}
