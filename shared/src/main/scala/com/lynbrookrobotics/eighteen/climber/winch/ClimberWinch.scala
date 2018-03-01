package com.lynbrookrobotics.eighteen.climber.winch

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import com.lynbrookrobotics.potassium.streams._
import squants.{Dimensionless, Each, Percent}

class ClimberWinch(val coreTicks: Stream[Unit])(implicit hardware: ClimberWinchHardware)
    extends Component[Dimensionless] {
  override def defaultController: Stream[Dimensionless] = coreTicks.mapToConstant(Each(0))

  private val checkL = new SingleOutputChecker(
    "Climber Winch Left Motor",
    hardware.leftMotor.getLastCommand
  )
  private val checkM = new SingleOutputChecker(
    "Climber Winch Middle Motor",
    hardware.leftMotor.getLastCommand
  )
  private val checkR = new SingleOutputChecker(
    "Climber Winch Right Motor",
    hardware.leftMotor.getLastCommand
  )

  override def applySignal(signal: Dimensionless) =
    checkL.assertSingleOutput(
      () =>
        checkM.assertSingleOutput(
          () =>
            checkR.assertSingleOutput(
              () =>
                if (signal < Percent(0)) applySignal(Percent(0)) // ratchet
                else {
                  hardware.leftMotor.applyCommand(OpenLoop(signal))
                  hardware.middleMotor.applyCommand(OpenLoop(signal))
                  hardware.rightMotor.applyCommand(OpenLoop(signal))
              }
          )
      )
    )
}
