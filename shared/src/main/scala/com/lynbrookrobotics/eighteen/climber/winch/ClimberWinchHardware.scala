package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.frc.LazyTalon
import squants.electro.{Amperes, ElectricCurrent}
import com.lynbrookrobotics.potassium.streams._

import scala.collection.Set

final case class ClimberWinchHardware(
  coreTicks: Stream[Unit],
  leftMotor: LazyTalon,
  middleMotor: LazyTalon,
  rightMotor: LazyTalon
) {
  Set(leftMotor, middleMotor, rightMotor).map(_.t).foreach { it =>
    it.setNeutralMode(NeutralMode.Brake)
  }

  val leftCurrent: Stream[ElectricCurrent] = coreTicks.map(_ => Amperes(leftMotor.t.getOutputCurrent))
  val midCurrent: Stream[ElectricCurrent] = coreTicks.map(_ => Amperes(middleMotor.t.getOutputCurrent))
  val rightCurrent: Stream[ElectricCurrent] = coreTicks.map(_ => Amperes(rightMotor.t.getOutputCurrent))
}

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig, coreTicks: Stream[Unit]): ClimberWinchHardware = {
    new ClimberWinchHardware(
      coreTicks, {
        println(s"[DEBUG] Creating climber winch left talon on port ${config.ports.leftMotorPort}")
        new LazyTalon(new TalonSRX(config.ports.leftMotorPort))
      }, {
        println(s"[DEBUG] Creating climber winch middle talon on port ${config.ports.middleMotorPort}")
        new LazyTalon(new TalonSRX(config.ports.middleMotorPort))
      }, {
        println(s"[DEBUG] Creating climber winch right talon on port ${config.ports.rightMotorPort}")
        new LazyTalon(new TalonSRX(config.ports.rightMotorPort))
      }
    )
  }
}
