package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.frc.LazyTalon

final case class ClimberWinchHardware(leftMotor: LazyTalon, middleMotor: LazyTalon, rightMotor: LazyTalon)

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig): ClimberWinchHardware = {
    new ClimberWinchHardware(
      {
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
