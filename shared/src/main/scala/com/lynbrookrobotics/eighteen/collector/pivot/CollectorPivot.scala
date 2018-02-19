package com.lynbrookrobotics.eighteen.collector.pivot

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampHardware
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait CollectorPivotState

case object PivotUpState extends CollectorPivotState
case object PivotDownState extends CollectorPivotState

class CollectorPivot(val coreTicks: Stream[Unit])(implicit hardware: CollectorClampHardware)
    extends Component[CollectorPivotState] {
  override def defaultController: Stream[CollectorPivotState] = coreTicks.mapToConstant(PivotDownState)

  override def applySignal(signal: CollectorPivotState): Unit = {
    signal match {
      case PivotUpState   => hardware.solenoid.set(true)
      case PivotDownState => hardware.solenoid.set(false)
    }
  }
}
