package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.commons.lift.Lift
import com.lynbrookrobotics.potassium.streams.Stream
import squants.{Dimensionless, Each}

class CubeLiftWrapper(val coreTicks: Stream[Unit])
                     (implicit hardware: CubeLiftHardware) extends Lift {

  override type Properties = CubeLiftProperties
  override type Hardware = CubeLiftHardware
  override type Comp = CubeLiftComponent

  class CubeLiftComponent extends Component[Dimensionless] {
    override def defaultController: Stream[Dimensionless] = coreTicks.mapToConstant(Each(0))

    override def applySignal(signal: Dimensionless): Unit = {
      hardware.spark.set(signal.toEach)
    }

  }
}