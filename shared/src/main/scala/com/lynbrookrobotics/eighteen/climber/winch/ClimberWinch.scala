package com.lynbrookrobotics.eighteen.climber.winch

import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.{streams, Component}
import squants.{Dimensionless, Each, Percent}
import com.lynbrookrobotics.potassium.streams._

class ClimberWinch(val coreTicks: Stream[Unit])(implicit hardware: ClimberWinchHardware)
    extends Component[Dimensionless] {
  override def defaultController: Stream[Dimensionless] = coreTicks.mapToConstant(Each(0))

  override def applySignal(signal: Dimensionless): Unit = {
    if (signal < Percent(0)) applySignal(Percent(0)) // ratchet
    else {
      hardware.leftMotor.set(ControlMode.PercentOutput, signal.toEach)
      hardware.middleMotor.set(ControlMode.PercentOutput, signal.toEach)
      hardware.rightMotor.set(ControlMode.PercentOutput, signal.toEach)
    }
  }
}
