package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal

package object drivetrain extends OffloadedDrive {
  override final type Properties = DrivetrainProperties
  override final type Hardware = DrivetrainHardware

  override protected def output(h: Hardware, s: TwoSided[OffloadedSignal]): Unit = ???
  override protected def controlMode(implicit hardware: Hardware, props: Properties) = ???

  type Drivetrain = DrivetrainComponent
}
