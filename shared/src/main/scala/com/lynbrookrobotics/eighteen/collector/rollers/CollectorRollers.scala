package com.lynbrookrobotics.eighteen.collector.rollers

import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream
import squants.{Dimensionless, Each}

class CollectorRollers(val coreTicks: Stream[Unit])(implicit hardware: CollectorRollersHardware) extends Component[(Dimensionless, Dimensionless)] {
  override def defaultController: Stream[(Dimensionless, Dimensionless)] = coreTicks.mapToConstant((Each(0), Each(0)))

  override def applySignal(signal: (Dimensionless, Dimensionless)): Unit = {
    hardware.rollerLeft.set(ControlMode.PercentOutput, signal._1.toEach)
    hardware.rollerRight.set(ControlMode.PercentOutput, signal._2.toEach)
  }
}

