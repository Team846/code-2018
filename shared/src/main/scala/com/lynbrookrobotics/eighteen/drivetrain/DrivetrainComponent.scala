package com.lynbrookrobotics.eighteen.drivetrain

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.{Each, Percent}

class DrivetrainComponent(coreTicks: Stream[Unit])(
  implicit hardware: DrivetrainHardware,
  props: Signal[DrivetrainProperties],
  clock: Clock
) extends Component[TwoSided[OffloadedSignal]] {
  override def defaultController: Stream[TwoSided[OffloadedSignal]] = {
    import hardware.driverHardware.station.{isEnabled, isOperatorControl}

    if (isEnabled && isOperatorControl) {
      val forward = hardware.driverHardware.joystickStream
        .map(v => -v.driver.y)
        .map(s => Each(Math.copySign((s * s).toEach, s.toEach)))

      val turn = hardware.driverHardware.joystickStream
        .map(v => v.driverWheel.x)
        .map(s => Each(Math.copySign((s * s).toEach, s.toEach)))

      UnicycleControllers.childVelocityControl(
        UnicycleControllers.speedControl(
          forward
            .zip(turn)
            .map(t => UnicycleSignal(t._1, t._2))
        )
      )
    } else {
      UnicycleControllers.childOpenLoop(
        hardware.forwardPosition
          .mapToConstant(
            UnicycleSignal(Percent(0), Percent(0))
          )
      )
    }
  }

  val checkLM = new SingleOutputChecker(
    "Drivetrain Left Master Talon",
    hardware.left.getLastCommand
  )

  val checkRM = new SingleOutputChecker(
    "Drivetrain Right Master Talon",
    hardware.right.getLastCommand
  )

  override def applySignal(signal: TwoSided[OffloadedSignal]) = checkLM.assertSingleOutput(() => checkRM.assertSingleOutput { () =>
    hardware.left.applyCommand(signal.left)
    hardware.right.applyCommand(signal.right)
  })
}
