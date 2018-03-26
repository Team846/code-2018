package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.MathUtilities
import com.lynbrookrobotics.potassium.streams.Stream
import squants.{Dimensionless, Each, Percent}

class CollectorRollers(val coreTicks: Stream[Unit])(
  implicit hardware: CollectorRollersHardware,
  driverHardware: DriverHardware
) extends Component[(Dimensionless, Dimensionless)] {
  override def defaultController: Stream[(Dimensionless, Dimensionless)] = {
    if (driverHardware.station.isEnabled) {
      coreTicks.mapToConstant((Percent(20), -Percent(20)))
    } else {
      coreTicks.mapToConstant((Each(0), Each(0)))
    }
  }

  private val check = new SingleOutputChecker(
    "Collector Rollers Talons (left, right)",
    (hardware.rollerLeft.get, hardware.rollerRight.get)
  )

  override def applySignal(signal: (Dimensionless, Dimensionless)): Unit = check.assertSingleOutput {
    hardware.rollerLeft.set(-signal._1.toEach)
    hardware.rollerRight.set(signal._2.toEach)
  }
}
