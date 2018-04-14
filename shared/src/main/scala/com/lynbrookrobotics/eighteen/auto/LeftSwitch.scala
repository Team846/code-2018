package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenCollector}
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.units.Point
import squants.Percent
import squants.space.{Degrees, Feet, Inches}
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import squants.time.Seconds

trait LeftSwitch extends AutoGenerator {
  import r._

  object LeftCenterSwitch {
    val leftCenterSwitchPoints = Seq(
      Point.origin,
      Point(
        -Inches(54.0),
        Inches(64.3)
      ),
      Point(
        -Inches(54.0),
        Inches(107.8) - Inches(5)
      )
    )

    val leftCenterSwitchPointsThirdCube = Seq(
      Point.origin,
      Point(
        -Inches(54.0) + Feet(2.5),
        Inches(64.3)
      ),
      Point(
        -Inches(54.0),
        Inches(107.8) - Inches(5)
      )
    )

    val prePickupSecondCubePoint = Point(
      -Inches(46.4),
      Inches(36.4) + Feet(1.25)
    )

    val prePickupThirdCubePoint = Point(
      -Inches(46.4),
      Inches(36.4) + Feet(1.5)
    )

    val pickupSecondCubePoint = Point(
      -Inches(26.5) + Inches(8),
      Inches(60.6) + Inches(8)
    )

    val pickupThirdCubePoint = Point(
      -Inches(26.5) + Inches(26),
      Inches(60.6) + Feet(1)
    )

    def driveFirstCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLiftComp: CubeLiftComp
    ): FiniteTask = {
      new FollowWayPoints(
        leftCenterSwitchPoints,
        tolerance = Inches(12),
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .withTimeout(centerSwitchDriveTimeOut)
        .andUntilDone(liftElevatorToSwitch(cubeLiftComp).toContinuous)
        .andUntilDone(
          new PivotDown(collectorPivot)
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

      driveFirstCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
        .then(
          new FollowWayPointsWithPosition( // BEGIN SECOND CUBE
            Seq(
              leftCenterSwitchPoints(leftCenterSwitchPoints.length - 1),
              leftCenterSwitchPoints(leftCenterSwitchPoints.length - 2),
              prePickupSecondCubePoint
            ),
            tolerance = Feet(2),
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
          )(drivetrain)
            .andUntilDone(
              pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp).and(
                new OpenCollector(collectorClamp)
              )
            )
            .then(
              pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
                .forDuration(Seconds(0.25))
            )
        )
        .then(
          new DriveBeyondStraight(
            -Inches(6),
            Inches(6),
            Degrees(5),
            Percent(50)
          )(drivetrain)
            .withTimeout(Seconds(0.5))
            .then(
              new FollowWayPointsWithPosition(
                leftCenterSwitchPoints.takeRight(2),
                tolerance = Feet(2),
                maxTurnOutput = Percent(100),
                cruisingVelocity = purePursuitCruisingVelocity,
                targetTicksWithingTolerance = 1,
                forwardBackwardMode = ForwardsOnly,
                position = pose,
                turnPosition = relativeAngle
              )(drivetrain).andUntilDone(
                liftElevatorToSwitch(cubeLiftComp).toContinuous
              )
            )
            .andUntilDone(
              new PivotDown(collectorPivot)
            )
        )
        .then(
          new FollowWayPointsWithPosition( // BEGIN THIRD CUBE
            Seq(
              leftCenterSwitchPoints(leftCenterSwitchPoints.length - 1),
              leftCenterSwitchPoints(leftCenterSwitchPoints.length - 2),
              prePickupThirdCubePoint
            ),
            tolerance = Feet(2),
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
          )(drivetrain)
            .andUntilDone(
              pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp).and(
                new OpenCollector(collectorClamp)
              )
            )
            .then(
              pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
                .forDuration(Seconds(0.25))
            )
        )
        .then(
          new DriveBeyondStraight(
            -Inches(6),
            Inches(6),
            Degrees(5),
            Percent(50)
          )(drivetrain)
            .withTimeout(Seconds(0.5))
            .then(
              new FollowWayPointsWithPosition(
                leftCenterSwitchPointsThirdCube.takeRight(2),
                tolerance = Feet(2),
                maxTurnOutput = Percent(100),
                cruisingVelocity = purePursuitCruisingVelocity,
                targetTicksWithingTolerance = 1,
                forwardBackwardMode = ForwardsOnly,
                position = pose,
                turnPosition = relativeAngle
              )(drivetrain).andUntilDone(
                liftElevatorToSwitch(cubeLiftComp).toContinuous
              )
            )
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
