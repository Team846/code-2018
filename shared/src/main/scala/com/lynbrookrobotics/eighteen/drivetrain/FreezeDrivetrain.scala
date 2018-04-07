package com.lynbrookrobotics.eighteen.drivetrain

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import squants.space.Feet

class FreezeDrivetrain(drive: DrivetrainComponent)(
  implicit hardware: DrivetrainHardware,
  props: Signal[DrivetrainProperties]
) extends ContinuousTask {
  override def onStart(): Unit = {
    drive.setController(positionControl(hardware.rootDataStream.map(_ => TwoSided(Feet(0), Feet(0)))))
  }

  override def onEnd(): Unit = {
    drive.resetToDefault()
  }
}
