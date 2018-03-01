package com.lynbrookrobotics.eighteen.collector.clamp

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait CollectorClampState

case object ClosedClamp extends CollectorClampState
case object OpenClamp extends CollectorClampState

class CollectorClamp(val coreTicks: Stream[Unit])(implicit hardware: CollectorClampHardware)
    extends Component[CollectorClampState] {
  override def defaultController: Stream[CollectorClampState] = coreTicks.mapToConstant(ClosedClamp)

  private val check = new SingleOutputChecker(
    "Collector Clamp Solenoid",
    hardware.solenoid.get
  )

  override def applySignal(signal: CollectorClampState): Unit =
    check.assertSingleOutput(
      () =>
        signal match {
          case ClosedClamp => hardware.solenoid.set(false)
          case OpenClamp   => hardware.solenoid.set(true)
      }
    )
}
