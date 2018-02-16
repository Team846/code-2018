package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.lift.{CubeLiftComp, CubeLiftHardware, CubeLiftProperties}
import com.lynbrookrobotics.potassium.commons.lift.Lift
import com.lynbrookrobotics.potassium.commons.lift.offloaded.OffloadedLift
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import squants.Dimensionless

package object cubeLift extends OffloadedLift {

  override type Properties = CubeLiftProperties
  override type Hardware = CubeLiftHardware
  override type Comp = CubeLiftComp
  override type LiftSignal = OffloadedSignal

}
