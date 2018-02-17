package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.potassium.units.Point
import squants.space.{Feet, Inches}

object StartingPose {
  val robotLength = Feet(3)
  val robotWidth = Feet(3)
  val startingPose = Point(-robotWidth / 2, robotLength / 2)

  val smallRoomFactor = Feet(5)
}

object SameSideSwitchAndScalePoints {
  import StartingPose._

  val toScalePoints = Seq(
    startingPose,
    Point(
      -Inches(41.3),
      Inches(285.2) - smallRoomFactor
    )
  )
  val backupPostScalePoints = Seq(
    toScalePoints.last,
    Point(
      -Inches(25.2),
      Inches(268.0) - smallRoomFactor
    ),
    Point(
      -Inches(0),
      Inches(268) - smallRoomFactor
    )
  )
  val pickupSecondCubePoints = Seq(
    backupPostScalePoints.last,
    Point(
      -Inches(41.8),
      Inches(228.3) - smallRoomFactor
    )
  )
  val pickupThirdCubePoints = Seq(
    pickupSecondCubePoints.last,
    Point(
      -Inches(62.1),
      Inches(218.8) - smallRoomFactor
    )
  )
  val backupPreThirdCubeDropOffPoints = Seq(
    pickupThirdCubePoints.last,
    Point(
      -Inches(0),
      Inches(251.3) - smallRoomFactor
    ),
    Point(
      -Inches(13.2),
      Inches(251.3) - smallRoomFactor
    )
  )
  val dropOffThirdCubePoints = Seq(
    backupPreThirdCubeDropOffPoints.last,
    Point(
      -Inches(41.8),
      Inches(285.0) - smallRoomFactor
    )
  )
}

object OppositeSwitchPointsSameSideScale {
  val pickupSecondCubePoints = Seq(
    SameSideSwitchAndScalePoints.backupPostScalePoints.last,
    Point(
      -Inches(48.1),
      Inches(232.1)
    ),
    Point(
      -Inches(151.2),
      Inches(232.1)
    ),
    Point(
      -Inches(159.6),
      Inches(220.7)
    )
  )
  val pickupThirdCubePoints = Seq(
    pickupSecondCubePoints.last,
    Point(
      -Inches(182.5),
      Inches(213.1)
    )
  )
}

object SameSideSwitchOppositeScalePoints {
  import StartingPose._

  val prePickupPoint = Point(-Inches(264.0), Inches(232.3))
  val toSwitchPoints = Seq(
    startingPose,
    Point(
      -Inches(18),
      Inches(133.5)
    ),
    Point(
      -Inches(25.6),
      Inches(155.3)
    ),
    Point(
      -Inches(37.6),
      Inches(155.3)
    )
  )

  val driveBackPostSwitch = Seq(
    // Don't use toSwitchPoints.last because we drive back 12 inches
    // after delivering to switch
    Point(
      -Inches(25.6),
      Inches(155.3)
    ),
    Point(
      -Inches(32.9),
      Inches(210.6)
    ),
    Point(
      -Inches(48.9),
      Inches(232.3)
    ),
    prePickupPoint
  )

  val pickupSecondCube = Seq(
    prePickupPoint,
    Point(
      -Inches(230.7),
      Inches(218.6)
    )
  )

  val preDropOffPoint = Point(
    -Inches(266.7),
    Inches(218.6))
  val backUpToDropOffSecondCube = Seq(
    pickupSecondCube.last,
    preDropOffPoint
  )
  val forwardsDropOffSecondCube = Seq(
    preDropOffPoint,
    Point(
      -Inches(232.813),
      Inches(278.0)
    )
  )

  val backUpToPickupThirdCube = Seq(
    forwardsDropOffSecondCube.last,
    prePickupPoint
  )
  val pickupThirdCube = Seq(
    prePickupPoint,
    Point(
      -Inches(203.8),
      Inches(215.3)
    )
  )
  val backUpToDropOffThirdCube = Seq(
    pickupThirdCube.last,
    preDropOffPoint
  )
  val forwardsDropOffThirdCube = forwardsDropOffSecondCube
}

object OppositeSideSwitchScalePoints {
  import StartingPose._

  val toScalePoints = Seq(
    startingPose,
    Point(
      -Inches(32.9),
      Inches(210.6)
    ),
    Point(
      -Inches(96.6),
      Inches(232.5)
    ),
    Point(
      -Inches(187),
      Inches(232.5)
    ),
    Point(
      -Inches(222.4),
      Inches(251.3)
    ),
    Point(
      -Inches(222.4),
      Inches(275.3)
    )
  )
}