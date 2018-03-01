package com.lynbrookrobotics.eighteen.collector.rollers

import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream
import squants.{Dimensionless, Each, Percent}
import com.lynbrookrobotics.eighteen.driver.DriverHardware

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

  override def applySignal(signal: (Dimensionless, Dimensionless)): Unit = {
    hardware.rollerLeft.set(signal._1.toEach)
    hardware.rollerRight.set(signal._2.toEach)
  }
}
