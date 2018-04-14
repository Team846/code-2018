package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenCollector}
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, SpinForCollect, SpinForPurge}
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

object SameSideSwitchAndScalePoints {
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
      -Inches(45.7) - Inches(6),
      Inches(280.9) - Feet(1) - smallRoomFactor
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
      -Inches(41.8) - Inches(6) - Inches(4) - Feet(1) + Inches(4) + Inches(2),
      Inches(228.3 + 6) - Inches(6) - smallRoomFactor
    )
  )

  val pickupThirdCubeAfterSwitchPoints = Seq(
    pickupSecondCubePoints.last,
    Point(
      -Inches(62.1) - Inches(9),
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
      -Inches(45.7) - Inches(6),
      Inches(280.9) - Feet(1) - smallRoomFactor
    )
  )
}

trait SameSideScale extends AutoGenerator {
  import r._
  object SameSideScale {
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
        wayPoints = SameSideSwitchAndScalePoints.toScalePoints,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 5,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .and(new WaitTask(Seconds(2)).then(liftElevatorToScale(cubeLift).toFinite))
        .then(
          new SpinForPurge(collectorRollers).forDuration(Seconds(0.5))
        )
        .andUntilDone(new PivotDown(collectorPivot))
    }

    def spinAroundPostScale(
      drivetrain: DrivetrainComponent,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new RotateByAngle(
        -Degrees(155),
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
      limeLightHardware: LimeLightHardware,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.pickupSecondCubePoints,
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
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.25))
        )
    }

    def pickupThirdCubeAfterScale(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      limeLightHardware: LimeLightHardware,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.pickupThirdCubeAfterScalePoints,
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
        wayPoints = SameSideSwitchAndScalePoints.dropOffThirdCubePoints,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 5,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .and(liftElevatorToScale(cubeLift).toFinite)
        .andUntilDone(new PivotDown(collectorPivot))
        .then(
          shootCubeScale(collectorRollers, collectorPivot, cubeLift)
        )
        .then(
          liftElevatorToCollect(cubeLift).toFinite
            .andUntilDone(new PivotDown(collectorPivot))
        )
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
        .withTimeout(Seconds(10))
        .then(
          spinAroundPostScale(drivetrain, pose, relativeAngle)
            .and(new WaitTask(Seconds(0.5)).then(liftElevatorToCollect(cubeLift).toFinite))
            .andUntilDone(new PivotDown(collectorPivot))
            .withTimeout(Seconds(10))
        )
        .then(
          pickupSecondCube(
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLift,
            limeLightHardware,
            pose,
            relativeAngle
          ).withTimeout(Seconds(5))
        ) // use third cube auto for 2nd cube
        .then(
          new RotateByAngle(
            Degrees(-180),
            Degrees(10),
            5
          )(drivetrain)
            .andUntilDone(new SpinForCollect(collectorRollers))
            .withTimeout(Seconds(2))
            .withTimeout(Seconds(5))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(6))
        )
        .then(
          spinAroundPostScale(drivetrain, pose, relativeAngle)
            .and(new WaitTask(Seconds(0.5)).then(liftElevatorToCollect(cubeLift).toFinite))
            .andUntilDone(new PivotDown(collectorPivot))
            .withTimeout(Seconds(10))
        )
        .then(
          pickupThirdCubeAfterScale(
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLift,
            limeLightHardware,
            pose,
            relativeAngle
          ).withTimeout(Seconds(10))
        )
        .then(
          new RotateByAngle(
            Degrees(180),
            Degrees(25),
            1
          )(drivetrain)
            .andUntilDone(new SpinForCollect(collectorRollers))
            .andUntilDone(new PivotDown(collectorPivot))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .andUntilDone(new PivotDown(collectorPivot))
            .withTimeout(Seconds(10))
        )
    }
  }
}
