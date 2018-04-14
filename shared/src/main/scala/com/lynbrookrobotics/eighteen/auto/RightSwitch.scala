package com.lynbrookrobotics.eighteen.auto

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
import squants.space.{Degrees, Feet, Inches}
import squants.{Angle, Percent}

trait RightSwitch extends AutoGenerator {
  import r._

  object RightCenterSwitch {
    val rightCenterSwitchPoints = Seq(
      Point.origin,
      Point(
        Inches(38.5),
        Inches(66.6)
      ),
      Point(
        Inches(38.5),
        Inches(107.8) - Inches(8)
      )
    )
    def driveFirstCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLiftComp: CubeLiftComp,
      pose: Stream[Point],
      angle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = rightCenterSwitchPoints,
        tolerance = Inches(6),
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly,
        position = pose,
        turnPosition = angle
      )(drivetrain)
        .andUntilDone(liftElevatorToSwitch(cubeLiftComp).toContinuous)
        .withTimeout(centerSwitchDriveTimeOut)
        .andUntilDone(
          new PivotDown(collectorPivot)
        )
    }

    val prePickupSecondCubePoint = Point(
      Inches(23.5),
      Inches(40.7)
    )

    val prePickupThirdCubePoint = Point(
      Inches(38.5),
      Inches(50.7)
    )

    val pickupSecondCubePoint = Point(
      Inches(23.5) - Inches(11.5) - Inches(3) - Inches(2),
      Inches(40.7) + Inches(19) + Inches(3)
    )

    val pickupThirdCubePoint = Point(
      Inches(23.5) - Inches(11.5) + Feet(0.75),
      Inches(40.7) + Inches(19) + Feet(1)
    )

    def pickUpSecondCube(
      pose: Stream[Point],
      relativeAngle: Stream[Angle],
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLiftComp: CubeLiftComp
    ) = {
      new FollowWayPointsWithPosition(
        Seq(
          rightCenterSwitchPoints(rightCenterSwitchPoints.length - 1),
          rightCenterSwitchPoints(rightCenterSwitchPoints.length - 2),
          prePickupSecondCubePoint
        ),
        tolerance = Inches(6),
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly,
        position = pose,
        turnPosition = relativeAngle
      )(drivetrain)
        .andUntilDone(
          new PivotDown(collectorPivot)
        )
        .and(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
        )
        .then(
          new RotateToAngle(
            -Degrees(30),
            Degrees(20)
          )(drivetrain)
            .andUntilDone(
              liftElevatorToCollect(cubeLiftComp).toContinuous
            )
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
        .then(
          new FollowWayPointsWithPosition(
            Seq(
              prePickupSecondCubePoint,
              pickupSecondCubePoint
            ),
            tolerance = Inches(6),
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly,
            position = pose,
            turnPosition = relativeAngle
          )(drivetrain).andUntilDone(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
          )
        )
    }

    def twoCubeCenterSwitch(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLiftComp: CubeLiftComp
    ): FiniteTask = {
      val relativeAngle = drivetrainHardware.turnPosition.relativize((init, curr) => {
        curr - init
      })

      val pose = XYPosition
        .circularTracking(
          relativeAngle.map(compassToTrigonometric),
          drivetrainHardware.forwardPosition
        )
        .preserve

      driveFirstCube(
        drivetrain,
        collectorRollers,
        collectorClamp,
        collectorPivot,
        cubeLiftComp,
        pose,
        relativeAngle
      ).then(
          pickUpSecondCube(
            pose,
            relativeAngle,
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLiftComp
          )
        )
        .then(
          new DriveBeyondStraight(
            -Inches(6),
            Inches(1),
            Degrees(5),
            Percent(20)
          )(drivetrain)
            .then(
              new RotateToAngle(
                Degrees(30),
                Degrees(20)
              )(drivetrain)
            )
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
        .then(
          new FollowWayPointsWithPosition(
            pickupSecondCubePoint +:
              rightCenterSwitchPoints.takeRight(2),
            tolerance = Inches(6),
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly,
            position = pose,
            turnPosition = relativeAngle
          )(drivetrain)
            .andUntilDone(liftElevatorToSwitch(cubeLiftComp).toContinuous)
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
        .then(
          new FollowWayPointsWithPosition(
            Seq(
              rightCenterSwitchPoints(rightCenterSwitchPoints.length - 1),
              rightCenterSwitchPoints(rightCenterSwitchPoints.length - 2),
              prePickupThirdCubePoint
            ),
            tolerance = Inches(6),
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = BackwardsOnly,
            position = pose,
            turnPosition = relativeAngle
          )(drivetrain)
            .and(
              dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
            )
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
        .then(
          new RotateToAngle(
            -Degrees(30),
            Degrees(20)
          )(drivetrain)
            .andUntilDone(
              liftElevatorToCollect(cubeLiftComp).toContinuous
            )
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
        .then(
          new FollowWayPointsWithPosition(
            Seq(
              prePickupThirdCubePoint,
              pickupThirdCubePoint
            ),
            tolerance = Inches(6),
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly,
            position = pose,
            turnPosition = relativeAngle
          )(drivetrain).andUntilDone(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
          )
        )
        .then(
          new DriveBeyondStraight(
            -Inches(6),
            Inches(1),
            Degrees(5),
            Percent(20)
          )(drivetrain)
            .then(
              new RotateToAngle(
                Degrees(30),
                Degrees(20)
              )(drivetrain)
            )
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
        .then(
          new FollowWayPointsWithPosition(
            pickupThirdCubePoint +: rightCenterSwitchPoints.takeRight(2),
            tolerance = Inches(6),
            maxTurnOutput = Percent(100),
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly,
            position = pose,
            turnPosition = relativeAngle
          )(drivetrain)
            .andUntilDone(liftElevatorToSwitch(cubeLiftComp).toContinuous)
            .then(
              dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
            )
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
    }
  }

}
