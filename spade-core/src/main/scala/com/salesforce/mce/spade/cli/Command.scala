package com.salesforce.mce.spade.cli

trait Command

object Command {

  case object Generate extends Command

  case object Create extends Command

  case object Activate extends Command

}
