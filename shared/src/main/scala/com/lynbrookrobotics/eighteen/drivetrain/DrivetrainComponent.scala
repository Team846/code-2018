package com.lynbrookrobotics.eighteen.drivetrain

import com.lynbrookrobotics.eighteen.{SingleOutputChecker, StallChecker}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.Task
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.time.Seconds
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

  private val check = new SingleOutputChecker(
    "Drivetrain Left Master Talon (left, right)",
    (hardware.left.getLastCommand, hardware.right.getLastCommand)
  )

  StallChecker
    .timeAboveThreshold(
      hardware.leftVelocity.zip(hardware.leftDutyCycle).map {
        case (currVelocity, dutyCycle) => (props.get.maxLeftVelocity * dutyCycle.toEach) - currVelocity
      },
      props.get.deltaVelocityStallThreshold
    )
    .filter(_ > props.get.stallTimeout)
    .foreach { time =>
      println(s"[ERROR] LEFT SIDE OF DRIVETRAIN STALLED FOR $time. ABORTING TASK.")
      Task.abortCurrentTask()
    }

  StallChecker
    .timeAboveThreshold(
      hardware.rightVelocity.zip(hardware.rightDutyCycle).map {
        case (currVelocity, dutyCycle) => (props.get.maxRightVelocity * dutyCycle.toEach) - currVelocity
      },
      props.get.deltaVelocityStallThreshold
    )
    .filter(_ > props.get.stallTimeout)
    .foreach { time =>
      println(s"[ERROR] RIGHT SIDE OF DRIVETRAIN STALLED FOR $time. ABORTING TASK.")
      Task.abortCurrentTask()
    }

  StallChecker
    .timeAboveThreshold(
      hardware.rightMasterCurrent.zip(hardware.rightFollowerCurrent).map { case (m, f) => (m - f).abs },
      props.get.parallelMotorCurrentThreshold
    )
    .filter(_ <= Seconds(0))
    .foreach { time =>
      println(s"[WARNING] RIGHT MASTER AND RIGHT FOLLOWER HAVE DIFFERENT CURRENT DRAWS. CHECK FUNKY DASHBOARD.")
    }

  StallChecker
    .timeAboveThreshold(
      hardware.leftMasterCurrent.zip(hardware.leftFollowerCurrent).map { case (m, f) => (m - f).abs },
      props.get.parallelMotorCurrentThreshold
    )
    .filter(_ <= Seconds(0))
    .foreach { time =>
      println(s"[WARNING] LEFT MASTER AND LEFT FOLLOWER HAVE DIFFERENT CURRENT DRAWS. CHECK FUNKY DASHBOARD.")
    }

  override def applySignal(signal: TwoSided[OffloadedSignal]): Unit = check.assertSingleOutput {
    hardware.left.applyCommand(signal.left)
    hardware.right.applyCommand(signal.right)
  }
}
