package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.can.TalonSRX

final case class ClimberWinchHardware(leftMotor: TalonSRX, middleMotor: TalonSRX, rightMotor: TalonSRX)

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig): ClimberWinchHardware = {
    println(
      s"Creating TalonSRXs on Ports ${config.ports.leftMotorPort}, ${config.ports.rightMotorPort}, ${config.ports.middleMotorPort}"
    )
    new ClimberWinchHardware(
      {
        println(s"Creating new TalonSRX (left motor) on Port ${config.ports.leftMotorPort}")
        new TalonSRX(config.ports.leftMotorPort)
      }, {
        println(s"Creating new TalonSRX (middle motor) on Port ${config.ports.middleMotorPort}")
        new TalonSRX(config.ports.middleMotorPort)
      }, {
        println(s"Creating new TalonSRX (middle motor) on Port ${config.ports.rightMotorPort}")
        new TalonSRX(config.ports.rightMotorPort)
      }
    )
  }
}
