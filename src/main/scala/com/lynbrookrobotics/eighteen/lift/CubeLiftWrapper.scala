package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.commons.lift.LiftProperties
import com.lynbrookrobotics.potassium.streams.Stream
import squants.Dimensionless

class Lift(val coreTicks: Stream[Unit])(implicit hardware: CubeLiftHardware) extends com.lynbrookrobotics.potassium.commons.lift.Lift {

  override type Properties = LiftProperties
  override type Hardware = CubeLiftHardware
  override type Comp = Stream[Dimensionless]


}
