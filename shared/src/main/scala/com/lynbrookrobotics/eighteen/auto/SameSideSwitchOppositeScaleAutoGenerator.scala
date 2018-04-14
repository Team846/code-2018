package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.units.Point
import squants.space.{Degrees, Inches}
import squants.time.Seconds
import squants.{Angle, Percent}

object SameSideSwitchOppositeScalePoints {
  import StartingPose._

  val prePickupPoint = Point(-Inches(264.0), Inches(232.3))
  val toSwitchPoints = Seq(
    startingPose,
    Point(
      -Inches(20),
      Inches(133.5) - Inches(6)
    ),
    Point(
      -Inches(25.6),
      Inches(155.3) - Inches(6)
    ),
    Point(
      -Inches(42),
      Inches(155.3) - Inches(6)
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

  val preDropOffPoint = Point(-Inches(266.7), Inches(218.6))
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

trait SameSideSwitchOppositeScaleAutoGenerator extends AutoGenerator with SameSideSwitch {
  import r._

  object SameSideSwitchOppositeScale {
    def driveBackPostSwitch(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new DriveDistanceStraight(-Inches(12), toleranceForward = Inches(3), Degrees(5), Percent(10))(drivetrain).then(
        new FollowWayPointsWithPosition(
          wayPoints = SameSideSwitchOppositeScalePoints.driveBackPostSwitch,
          tolerance = Inches(3),
          position = pose,
          turnPosition = relativeAngle,
          maxTurnOutput = Percent(100),
          cruisingVelocity = purePursuitCruisingVelocity,
          targetTicksWithingTolerance = 10,
          forwardBackwardMode = BackwardsOnly
        )(drivetrain)
      )
    }

    val cubePickupAndDropOffDriveTimout = Seconds(3)

    def pickupSecondCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.pickupSecondCube,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).andUntilDone(
        pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )
    }

    def pickUpThirdCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.backUpToPickupThirdCube,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain)
        .then(
          new FollowWayPointsWithPosition(
            wayPoints = SameSideSwitchOppositeScalePoints.pickupThirdCube,
            tolerance = Inches(3),
            position = pose,
            turnPosition = relativeAngle,
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 10,
            forwardBackwardMode = ForwardsOnly
          )(drivetrain)
        )
        .andUntilDone(
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
    }

    def dropOffSecondCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.backUpToDropOffSecondCube,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain)
        .andUntilDone(
          CollectorTasks
            .collectCubeWithoutOpen(collectorRollers, collectorPivot)
            .forDuration(Seconds(1))
            .toContinuous
        )
        .then(
          new FollowWayPointsWithPosition(
            wayPoints = SameSideSwitchOppositeScalePoints.forwardsDropOffSecondCube,
            tolerance = Inches(6),
            position = pose,
            turnPosition = relativeAngle,
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly
          )(drivetrain)
        )
        .then(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
    }

    def dropOffThirdCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.backUpToDropOffThirdCube,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain)
        .andUntilDone(
          CollectorTasks
            .collectCubeWithoutOpen(collectorRollers, collectorPivot)
            .forDuration(Seconds(1))
            .toContinuous
        )
        .then(
          new FollowWayPointsWithPosition(
            wayPoints = SameSideSwitchOppositeScalePoints.forwardsDropOffThirdCube,
            tolerance = Inches(6),
            position = pose,
            turnPosition = relativeAngle,
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly
          )(drivetrain)
        )
        .then(
          shootCubeScale(collectorRollers, collectorPivot, cubeLift)
        )
        .then(
          liftElevatorToCollect(cubeLift).toFinite
        )
    }

    def switchScaleScale(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp
    ): FiniteTask = {
      val relativeAngle = drivetrainHardware.turnPosition.relativize((init, curr) => {
        curr - init
      })

      val pose = XYPosition
        .circularTracking(
          relativeAngle.map(compassToTrigonometric),
          drivetrainHardware.forwardPosition
        )
        .map(
          p => p + sideStartingPose
        )
        .preserve

      SameSideSwitch.dropOffToSwitch(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        .withTimeout(Seconds(5))
        .then(
          driveBackPostSwitch(drivetrain, collectorRollers, collectorClamp, pose, relativeAngle).withTimeout(Seconds(3))
        )
        .then(
          pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(5))
        )
        .then(
          dropOffSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(5))
        )
        .then(
          pickUpThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(5))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(3))
        )
    }
  }
}
