package com.lynbrookrobotics.eighteen.collector.pivot

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampHardware
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait CollectorPivotState

case object PivotUp extends CollectorPivotState
case object PivotDown extends CollectorPivotState

class CollectorPivot(val coreTicks: Stream[Unit])(implicit hardware: CollectorClampHardware) extends Component[CollectorPivotState]{
  override def defaultController: Stream[CollectorPivotState] = coreTicks.mapToConstant(PivotDown)

  override def applySignal(signal: CollectorPivotState): Unit = {
    signal match {
      case PivotUp => hardware.solenoid.set(true)
      case PivotDown => hardware.solenoid.set(false)
    }
  }
}