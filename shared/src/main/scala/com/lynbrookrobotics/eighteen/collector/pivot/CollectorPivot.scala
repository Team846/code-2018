package com.lynbrookrobotics.eighteen.collector.pivot

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.eighteen.JoystickButtons._

trait CollectorPivotState

case object PivotUpState extends CollectorPivotState
case object PivotDownState extends CollectorPivotState

class CollectorPivot(driverHardware: DriverHardware, val coreTicks: Stream[Unit])(
  implicit hardware: CollectorPivotHardware
) extends Component[CollectorPivotState] {
  override def defaultController: Stream[CollectorPivotState] =
    coreTicks
      .map(_ => driverHardware.operatorJoystick.getRawButton(LeftTwo))
      .map(it => if (it) PivotDownState else PivotUpState)

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
