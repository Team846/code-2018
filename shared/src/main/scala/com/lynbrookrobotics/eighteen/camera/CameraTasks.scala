package com.lynbrookrobotics.eighteen.camera

import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.vision.limelight.LimeLightHardware
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import squants.Percent
import squants.space.Length

object CameraTasks {
  def visionCubePickup(drivetrain: DrivetrainComponent,
                       camera: LimeLightHardware,
                       minDistance: Length,
                       rollers: CollectorRollers,
                       clamp: CollectorClamp,
                       pivot: CollectorPivot): FiniteTask = {

    new DriveToTargetWithConstantSpeed(
      drivetrain,
      camera.distanceToTarget,
      camera.angleToTarget,
      Percent(20),
      Percent(20),
      minDistance).then(
      CollectorTasks.collectUntilCubeIn(
        rollers,
        clamp,
        pivot
      )
    )
  }
}
