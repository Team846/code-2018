package com.lynbrookrobotics.eighteen.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import squants.Each


package object drivetrain extends OffloadedDrive {
  override type Hardware = DrivetrainHardware
  override type Properties = DrivetrainProperties

  override protected def output(hardware: Hardware, signal: TwoSided[OffloadedSignal]): Unit = {
    if(Math.random()>0.999) {
      println(s"rsig: ${signal.right}")
      println(s"lsig: ${signal.left}")
      println(s"lfee: ${hardware.leftEncoder.getAngularVelocity}")
      println(s"lfee: ${hardware.rightEncoder.getAngularVelocity}")
      println()
    }
    hardware.leftMaster.applyCommand(signal.left)
    hardware.rightMaster.applyCommand(signal.right)
  }

  override protected def controlMode(implicit hardware: Hardware, props: Properties): UnicycleControlMode = {
    NoOperation
  }
}
