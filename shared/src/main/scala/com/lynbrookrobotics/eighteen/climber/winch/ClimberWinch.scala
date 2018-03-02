package com.lynbrookrobotics.eighteen.climber.winch

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import com.lynbrookrobotics.potassium.streams._
import squants.{Dimensionless, Each, Percent}

class ClimberWinch(val coreTicks: Stream[Unit])(implicit hardware: ClimberWinchHardware)
  extends Component[Dimensionless] {
  override def defaultController: Stream[Dimensionless] = coreTicks.mapToConstant(Each(0))

  private val check = new SingleOutputChecker(
    "Climber Winch Talons (left, middle, right)",
    (hardware.leftMotor.getLastCommand, hardware.middleMotor.getLastCommand, hardware.rightMotor.getLastCommand)
  )

  override def applySignal(signal: Dimensionless): Unit = check.assertSingleOutput {
    if (signal < Percent(0)) applySignal(Percent(0)) // ratchet
    else {
      hardware.leftMotor.applyCommand(OpenLoop(signal))
      hardware.middleMotor.applyCommand(OpenLoop(signal))
      hardware.rightMotor.applyCommand(OpenLoop(signal))
    }
  }
}
