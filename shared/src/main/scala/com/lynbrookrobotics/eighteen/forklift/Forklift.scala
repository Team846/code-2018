package com.lynbrookrobotics.eighteen.forklift

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait ForkliftState

case object ForkliftUp extends ForkliftState

case object ForkliftDown extends ForkliftState

class Forklift(val coreTicks: Stream[Unit])(implicit hardware: ForkliftHardware) extends Component[ForkliftState] {
  override def defaultController: Stream[ForkliftState] = coreTicks.mapToConstant(ForkliftUp)

  private val check = new SingleOutputChecker(
    "Forklift Solenoid",
    hardware.forkliftSolenoid.get
  )

  override def applySignal(signal: ForkliftState): Unit = check.assertSingleOutput {
        signal match {
          case ForkliftUp => {
            hardware.forkliftSolenoid.set(false)
          }
          case ForkliftDown => {
            hardware.forkliftSolenoid.set(true)
          }
      }
    }
}
