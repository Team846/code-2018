package com.lynbrookrobotics.eighteen.camera

import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.drivetrain.{DrivetrainComponent, DrivetrainHardware, DrivetrainProperties}
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.vision.limelight.LimeLightHardware
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, CollectorClampHardware, CollectorClampProps}
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, CollectorRollersProperties}
import com.lynbrookrobotics.potassium.Signal
import squants.Percent
import squants.space.Length

object CameraTasks {
  def visionCubePickup(drivetrain: DrivetrainComponent,
                       camera: LimeLightHardware,
                       minDistance: Length,
                       rollers: CollectorRollers,
                       clamp: CollectorClamp,
                       pivot: CollectorPivot)
                      (implicit drivetrainHardware: DrivetrainHardware,
                       drivetrainProperties: Signal[DrivetrainProperties],
                       collectorClampProps: Signal[CollectorClampProps],
                       collectorRollerProps: Signal[CollectorRollersProperties],
                       collectorClampHardware: CollectorClampHardware): FiniteTask = {

    new DriveToTargetWithConstantSpeed(
      drivetrain,
      camera.distanceToTarget,
      camera.angleToTarget,
      Percent(20),
      Percent(20),
      minDistance)(drivetrainHardware, drivetrainProperties).then(
      CollectorTasks.collectUntilCubeIn(
        rollers,
        clamp,
        pivot
      )(collectorClampProps, collectorClampHardware, collectorRollerProps)
    )
  }
}
