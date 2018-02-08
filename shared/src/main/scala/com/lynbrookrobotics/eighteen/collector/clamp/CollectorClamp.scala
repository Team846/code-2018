package com.lynbrookrobotics.eighteen.collector.clamp

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait CollectorClampState

case object ClosedClamp extends CollectorClampState
case object OpenClamp extends CollectorClampState

class CollectorClamp(val coreTicks: Stream[Unit])(implicit hardware: CollectorClampHardware) extends Component[CollectorClampState]{
  override def defaultController: Stream[CollectorClampState] = coreTicks.mapToConstant(OpenClamp)

  override def applySignal(signal: CollectorClampState): Unit = {
    signal match {
      case ClosedClamp => hardware.solenoid.set(true)
      case OpenClamp => hardware.solenoid.set(false)
    }
  }
}
