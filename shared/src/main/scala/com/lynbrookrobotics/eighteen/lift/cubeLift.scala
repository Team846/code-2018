package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.lift.{CubeLiftComp, CubeLiftHardware, CubeLiftProperties}
import com.lynbrookrobotics.potassium.commons.lift.offloaded.OffloadedLift
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal

package object cubeLift extends OffloadedLift {
  override type Properties = CubeLiftProperties
  override type Hardware = CubeLiftHardware
  override type Comp = CubeLiftComp
  override type LiftSignal = OffloadedSignal
}
