package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.commons.drivetrain.UnicycleControlMode
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.commons.lift.Lift
import com.lynbrookrobotics.potassium.commons.lift.offloaded.OffloadedLift
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import squants.Dimensionless

package object cubeLift extends OffloadedLift {

  override type Properties = CubeLiftProperties
  override type Hardware = CubeLiftHardware

}