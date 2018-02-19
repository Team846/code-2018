package com.lynbrookrobotics.eighteen.camera

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.vision.VisionTargetTracking
import com.lynbrookrobotics.potassium.vision.limelight.LimelightNetwork
import squants.space.{Degrees, Feet}

class CameraHardware {
  val camera = LimelightNetwork(WPIClock)
  val tracker = new VisionTargetTracking(
    cameraHorizontalOffset = Signal.constant(Degrees(0)),
    distanceConstant = Signal.constant(Feet(10.8645)))
  val distanceToTarget = tracker.distanceToTarget(camera.percentArea)
  val angleToTarget = tracker.angleToTarget(camera.xOffsetAngle)
  val hasTarget = camera.hasTarget
  camera.table.getEntry("ledMode").setDouble(1)
}
