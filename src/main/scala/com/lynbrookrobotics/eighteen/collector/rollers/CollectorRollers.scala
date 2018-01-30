package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Milliseconds
import squants.{Dimensionless, Each}

class CollectorRollers(val coreTicks: Stream[Unit])(implicit hardware: CollectorRollersHardware) extends Component[Dimensionless] {
  override def defaultController: Stream[Dimensionless] = coreTicks.mapToConstant(Each(0))

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.rollerLeft.set(signal.toEach)
    hardware.rollerRight.set(signal.toEach)
  }
}
