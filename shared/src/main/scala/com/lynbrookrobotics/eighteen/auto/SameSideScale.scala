package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenCollector}
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, SpinForCollect}
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.ForwardsOnly
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.tasks.WaitTask
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.potassium.vision.limelight.LimeLightHardware
import squants.{Angle, Percent, Seconds}
import squants.space.{Degrees, Feet, Inches}

trait SameSideScale extends AutoGenerator {
  import r._

  object SameSideScale {
    import StartingPose._

    val toScalePoints = Seq(
      startingPose,
      Point(
        startingPose.x,
        Inches(162)
      ),
      Point(
        -Inches(46.7),
        Inches(241)
      ),
      Point(
        -Inches(45.7) - Inches(6) - Inches(6) + Feet(0.5),
        Inches(280.9) - Feet(1.5) - smallRoomFactor
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
      Point(
        toScalePoints.last.x,
        toScalePoints.last.y - Feet(1)
      ),
      Point(
        -Inches(41.8) - Inches(4) - Feet(0.8),
        Inches(228.3 + 6 - 10) - smallRoomFactor
      )
    )

    val pickupThirdCubeAfterSwitchPoints = Seq(
      pickupSecondCubePoints.last,
      Point(
        -Inches(62.1) - Inches(9) - Inches(4),
        Inches(218.8) + Inches(4) - smallRoomFactor
      )
    )

    val pickupThirdCubeAfterScalePoints = Seq(
      toScalePoints.last,
      pickupThirdCubeAfterSwitchPoints.last
    )

    val dropOffThirdCubePoints = Seq(
      pickupThirdCubeAfterScalePoints.last,
      Point(
        -Inches(46.7),
        Inches(241)
      ),
      Point(
        -Inches(45.7) - Inches(6) - Inches(6),
        Inches(280.9) - Feet(2) - smallRoomFactor
      )
    )

    def startToScaleDropOff(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = toScalePoints.map(invertXIfFromLeft),
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 2,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .and(new WaitTask(Seconds(2)).then(liftElevatorToScale(cubeLift).toFinite))
        .then(
          shootCubeScale(collectorRollers, collectorPivot, cubeLift)
        )
        .andUntilDone(new PivotDown(collectorPivot))
    }

    def spinAroundPostScale(
      drivetrain: DrivetrainComponent,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new RotateByAngle(
        invertIfFromLeft(-Degrees(115)),
        Degrees(25),
        1
      )(drivetrain)
    }

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
        wayPoints = pickupSecondCubePoints.map(invertXIfFromLeft),
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .andUntilDone(
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).and(
            new OpenCollector(collectorClamp)
          )
        )
        .then(
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(1))
        )
    }

    def pickupThirdCubeAfterScale(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = pickupThirdCubeAfterScalePoints.map(invertXIfFromLeft),
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .andUntilDone(
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).and(
            new OpenCollector(collectorClamp)
          )
        )
        .then(
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.5))
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
        wayPoints = dropOffThirdCubePoints.map(invertXIfFromLeft),
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(40),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 2,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .and(liftElevatorToScale(cubeLift).toFinite)
        .then(
          shootCubeScale(collectorRollers, collectorPivot, cubeLift)
        )
        .andUntilDone(new PivotDown(collectorPivot))
    }

    def oneInScale(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      limeLightHardware: LimeLightHardware
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

      startToScaleDropOff(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        .withTimeout(Seconds(8))
    }

    def threeInScale(
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

      startToScaleDropOff(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        .withTimeout(Seconds(10))
        .then(
          spinAroundPostScale(drivetrain, pose, relativeAngle)
            .and(new WaitTask(Seconds(0.75)).then(liftElevatorToCollect(cubeLift).toFinite))
            .andUntilDone(new PivotDown(collectorPivot))
            .withTimeout(Seconds(2))
        )
        .then(
          pickupSecondCube(
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLift,
            pose,
            relativeAngle
          ).withTimeout(Seconds(5))
        ) // use third cube auto for 2nd cube
        .then(
          new RotateByAngle(
            invertIfFromLeft(Degrees(-180)),
            Degrees(25),
            5
          )(drivetrain)
            .andUntilDone(new SpinForCollect(collectorRollers))
            .withTimeout(Seconds(2))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(6))
        )
        .then(
          spinAroundPostScale(drivetrain, pose, relativeAngle)
            .and(new WaitTask(Seconds(0.75)).then(liftElevatorToCollect(cubeLift).toFinite))
            .andUntilDone(new PivotDown(collectorPivot))
            .withTimeout(Seconds(2))
        )
        .then(
          pickupThirdCubeAfterScale(
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLift,
            pose,
            relativeAngle
          ).withTimeout(Seconds(5))
        )
        .then(
          new RotateByAngle(
            invertIfFromLeft(Degrees(210)),
            Degrees(25),
            1
          )(drivetrain)
            .andUntilDone(new SpinForCollect(collectorRollers))
            .andUntilDone(new PivotDown(collectorPivot))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .andUntilDone(new PivotDown(collectorPivot))
            .withTimeout(Seconds(5))
        )
    }
  }
}
