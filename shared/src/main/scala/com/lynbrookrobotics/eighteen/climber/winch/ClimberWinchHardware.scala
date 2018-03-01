package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.can.TalonSRX

final case class ClimberWinchHardware(leftMotor: TalonSRX, middleMotor: TalonSRX, rightMotor: TalonSRX)

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig): ClimberWinchHardware = {
    new ClimberWinchHardware(
      {
        println(s"Creating climber winch left talon on port ${config.ports.leftMotorPort}")
        new TalonSRX(config.ports.leftMotorPort)
      }, {
        println(s"Creating climber winch middle talon on port ${config.ports.middleMotorPort}")
        new TalonSRX(config.ports.middleMotorPort)
      }, {
        println(s"Creating climber winch right talon on port ${config.ports.rightMotorPort}")
        new TalonSRX(config.ports.rightMotorPort)
      }
    )
  }
}
