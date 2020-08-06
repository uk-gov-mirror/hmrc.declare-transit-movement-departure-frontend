package models

sealed trait Status

case object NotStarted extends Status
case object Incomplete extends Status
case object Complete extends Status
