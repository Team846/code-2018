package com.lynbrookrobotics.eighteen.collector.pivot

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait CollectorPivotState

case object PivotUpState extends CollectorPivotState
case object PivotDownState extends CollectorPivotState

class CollectorPivot(val coreTicks: Stream[Unit])(implicit hardware: CollectorPivotHardware)
    extends Component[CollectorPivotState] {
  override def defaultController: Stream[CollectorPivotState] = coreTicks.mapToConstant(PivotUpState)

  override def applySignal(signal: CollectorPivotState): Unit = {
    signal match {
      case PivotUpState   => hardware.pivotSolenoid.set(false)
      case PivotDownState => hardware.pivotSolenoid.set(true)
    }
  }
}
