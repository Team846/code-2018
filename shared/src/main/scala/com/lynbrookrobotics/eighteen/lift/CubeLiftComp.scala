package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.eighteen.{SingleOutputChecker, StallChecker}
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.Task
import com.lynbrookrobotics.potassium.{Component, Signal}
import edu.wpi.first.wpilibj.RobotState
import squants.electro.Volts
import squants.{Each, Percent}

class CubeLiftComp(val coreTicks: Stream[Unit])(implicit hardware: CubeLiftHardware, props: Signal[CubeLiftProperties])
    extends Component[OffloadedSignal] {
  override def defaultController: Stream[OffloadedSignal] = coreTicks.mapToConstant(OpenLoop(Each(0)))

  private val check = new SingleOutputChecker(
    "Cube Lift Talon",
    hardware.talon.getLastCommand
  )

  StallChecker
    .timeAboveThreshold(
      hardware.currentDraw,
      props.get.maxCurrentDraw
    )
    .filter(_ => RobotState.isAutonomous)
    .filter(_ > props.get.stallTimeout)
    .foreach { stallTime =>
      println(s"[ERROR] CUBE LIFT STALLED FOR $stallTime. ABORTING TASK.")
      Task.abortCurrentTask()
    }

  override def applySignal(signal: OffloadedSignal): Unit = check.assertSingleOutput {
    signal match {
      case OpenLoop(s) =>
        if (s.abs > Percent(20)) applySignal(OpenLoop(Percent(20) * s.value.signum))
        else hardware.talon.applyCommand(signal)
      case _ =>
        if (hardware.readPotentiometerVoltage == Volts(0)) {
          applySignal(OpenLoop(Percent(0)))
          println("[ERROR] No cube lift potentiometer detected")
        } else hardware.talon.applyCommand(signal)
    }
  }
}
