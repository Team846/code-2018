package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.commons.lift.Lift

package object cubeLift extends Lift {

  override type Properties = CubeLiftProperties
  override type Hardware = CubeLiftHardware
  override type Comp = CubeLiftComp

}