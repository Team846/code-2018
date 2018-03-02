package com.lynbrookrobotics.eighteen.collector.pivot

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait CollectorPivotState

case object PivotUpState extends CollectorPivotState
case object PivotDownState extends CollectorPivotState

class CollectorPivot(val coreTicks: Stream[Unit])(implicit hardware: CollectorPivotHardware)
    extends Component[CollectorPivotState] {
  override def defaultController: Stream[CollectorPivotState] = coreTicks.mapToConstant(PivotUpState)

  private val check = new SingleOutputChecker(
    "Collector Pivot Solenoid",
    hardware.pivotSolenoid.get
  )

  override def applySignal(signal: CollectorPivotState): Unit =
    check.assertSingleOutput {
      signal match {
        case PivotUpState   => hardware.pivotSolenoid.set(false)
        case PivotDownState => hardware.pivotSolenoid.set(true)
      }
    }
}
