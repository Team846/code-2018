package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.lift.{CubeLiftComp, CubeLiftHardware, CubeLiftProperties}
import com.lynbrookrobotics.potassium.commons.lift.Lift
import squants.Dimensionless

package object cubeLift extends Lift {

  override type Properties = CubeLiftProperties
  override type Hardware = CubeLiftHardware
  override type Comp = CubeLiftComp
  override type LiftSignal = Dimensionless

  override def openLoopToLiftSignal(x: Dimensionless) = x
}