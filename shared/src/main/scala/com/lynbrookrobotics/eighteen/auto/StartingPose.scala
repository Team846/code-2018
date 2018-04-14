package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.potassium.units.Point
import squants.space.Feet

object StartingPose {
  val robotLength = Feet(3)
  val robotWidth = Feet(3)
  val startingPose = Point(-robotWidth / 2, robotLength / 2)

  val smallRoomFactor = Feet(0)
}
