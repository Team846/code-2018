package com.lynbrookrobotics.eighteen

import argonaut.Argonaut._
import org.scalatest.FunSuite

import scala.io.Source

class ConfigValidTest extends FunSuite {
  test("Default config is valid") {
    val decoded = DefaultConfig.json.decodeEither[RobotConfig](RobotConfig.reader)
    decoded.left.foreach(s => throw new Exception(s))
  }

  test("robot-config.json is valid") {
    val decoded = Source.fromFile("robot-config.json").mkString.decodeEither[RobotConfig](RobotConfig.reader)
    decoded.left.foreach(s => throw new Exception(s))
  }

  test("practice-robot-config.json is valid") {
    val decoded = Source.fromFile("practice-robot-config.json").mkString.decodeEither[RobotConfig](RobotConfig.reader)
    decoded.left.foreach(s => throw new Exception(s))
  }
}
