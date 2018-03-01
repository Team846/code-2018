package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.frc.LazyTalon

final case class ClimberWinchHardware(leftMotor: LazyTalon, middleMotor: LazyTalon, rightMotor: LazyTalon)

object ClimberWinchHardware {
  def apply(config: ClimberWinchConfig): ClimberWinchHardware = {
    new ClimberWinchHardware(
      new LazyTalon(new TalonSRX(config.ports.leftMotorPort)),
      new LazyTalon(new TalonSRX(config.ports.middleMotorPort)),
      new LazyTalon( new TalonSRX(config.ports.rightMotorPort))
    )
  }
}
