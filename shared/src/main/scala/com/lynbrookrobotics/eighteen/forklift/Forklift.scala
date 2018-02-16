package com.lynbrookrobotics.eighteen.forklift

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait ForkliftState

case object ForkliftUp extends ForkliftState
case object ForkliftDown extends ForkliftState

class Forklift(val coreTicks: Stream[Unit])(implicit hardware: ForkliftHardware) extends Component[ForkliftState] {
  override def defaultController: Stream[ForkliftState] = coreTicks.mapToConstant(ForkliftUp)

  override def applySignal(signal: ForkliftState): Unit = signal match {
    case ForkliftUp => {
      hardware.solenoidLeft.set(true)
      hardware.solenoidRight.set(true)
    }
    case ForkliftDown => {
      hardware.solenoidLeft.set(false)
      hardware.solenoidRight.set(false)
    }
  }
}
