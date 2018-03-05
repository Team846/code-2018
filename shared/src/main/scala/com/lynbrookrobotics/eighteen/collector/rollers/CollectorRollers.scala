package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds
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

  override def setController(controller: Stream[(Dimensionless, Dimensionless)]): Unit = {
    val l = CurrentLimiting.slewRate(
      Each(hardware.rollerLeft.get),
      controller.map(_._1),
      Percent(100) / Seconds(0.3)
    )

    val r = CurrentLimiting.slewRate(
      Each(hardware.rollerRight.get),
      controller.map(_._2),
      Percent(100) / Seconds(0.3)
    )

    super.setController(l.zip(r))
  }

  override def applySignal(signal: (Dimensionless, Dimensionless)): Unit = check.assertSingleOutput {
    hardware.rollerLeft.set(-signal._1.toEach)
    hardware.rollerRight.set(signal._2.toEach)
  }
}
