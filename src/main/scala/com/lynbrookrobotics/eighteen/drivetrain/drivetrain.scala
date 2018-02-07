package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.{ArcadeControlsOpen, NoOperation}
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.Each

package object drivetrain extends OffloadedDrive { self =>
  override type Properties = DrivetrainProperties
  override type Hardware = DrivetrainHardware

  override protected def output(h: Hardware, s: TwoSided[OffloadedSignal]): Unit = {
    h.left.applyCommand(s.left)
    h.right.applyCommand(s.right)
  }

  override protected def controlMode(implicit hardware: Hardware, props: Properties) = {
    if (hardware.driverHardware.station.isEnabled && hardware.driverHardware.station.isOperatorControl) {
      ArcadeControlsOpen(
        hardware.driverHardware.joystickStream.map(v => -v.driver.y).map(s =>
          Each(Math.copySign((s * s).toEach, s.toEach))).syncTo(hardware.leftPosition),
        hardware.driverHardware.joystickStream.map(v => v.driverWheel.x).map(s =>
          Each(Math.copySign((s * s).toEach, s.toEach))).syncTo(hardware.leftPosition)
      )
    } else {
      NoOperation
    }
  }

  class Drivetrain(coreTicks: Stream[Unit])(implicit hardware: Hardware,
                   props: Signal[Properties],
                   clock: Clock) extends Component[DriveSignal] {
    override def defaultController: Stream[DriveSignal] = self.defaultController

    override def applySignal(signal: TwoSided[OffloadedSignal]) = output(hardware, signal)
  }
}
