package com.lynbrookrobotics.eighteen.auto


import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.cubeLift.positionTasks._
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, WrapperTask}
import com.lynbrookrobotics.potassium.units.Point
import squants.motion.{FeetPerSecond, FeetPerSecondSquared}
import squants.space.{Degrees, Feet, Inches}
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Percent}

trait CenterSwitch extends AutoGenerator {
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
        Inches(107.8)
      )
    )
    def dropOffFirstCube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLiftComp: CubeLiftComp,
                         pose: Stream[Point],
                         angle: Stream[Angle]
                         ): FiniteTask = {
      new FollowWayPointsWithPosition(
        rightCenterSwitchPoints,
        tolerance = Inches(6),
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly,
        position = pose,
        turnPosition = angle
      )(drivetrain)
        .withTimeout(centetSwitchDriveTimeOut)
        .then(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
        )
    }

    val prePickupSecondCubePoint = Point(
      Inches(23.5),
      Inches(40.7)
    )
    val pickupSecondCubePoint = Point(
      Inches(23.5) - Inches(11.5),
      Inches(40.7) + Inches(19)
    )

    def twoCubeCenterSwitch(drivetrain: DrivetrainComponent,
                            collectorRollers: CollectorRollers,
                            collectorClamp: CollectorClamp,
                            collectorPivot: CollectorPivot,
                            cubeLiftComp: CubeLiftComp
                        ): FiniteTask = {


      val pose = XYPosition.circularTracking(
        drivetrainHardware.turnPosition,
        drivetrainHardware.forwardPosition
      )
      dropOffFirstCube(
        drivetrain,
        collectorRollers,
        collectorClamp,
        collectorPivot,
        cubeLiftComp,
        pose,
        drivetrainHardware.turnPosition
      ).then(
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
          turnPosition = drivetrainHardware.turnPosition
        )(drivetrain).then(
          new RotateToAngle(
            -Degrees(30),
            Degrees(10)
          )(drivetrain)
        ).andUntilDone(
          liftElevatorToCollect(cubeLiftComp).toContinuous
        ).then(
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
            turnPosition = drivetrainHardware.turnPosition
          )(drivetrain).andUntilDone(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
          ).then(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp).forDuration(Seconds(1))
          ).then(
            new RotateToAngle(
              Degrees(30),
              Degrees(10)
            )(drivetrain)
          ).then(
            new FollowWayPointsWithPosition(
              Seq(
                pickupSecondCubePoint,
                rightCenterSwitchPoints.last
              ),
              tolerance = Inches(6),
              maxTurnOutput = Percent(100),
              cruisingVelocity = purePursuitCruisingVelocity,
              targetTicksWithingTolerance = 1,
              forwardBackwardMode = ForwardsOnly,
              position = pose,
              turnPosition = drivetrainHardware.turnPosition
            )(drivetrain)
          )
        )
      )
    }
  }

  object LeftCenterSwitch {
    val leftCenterSwitchPoints = Seq(
      Point.origin,
      Point(
        -Inches(54.0),
        Inches(64.3)
      ),
      Point(
        -Inches(54.0),
        Inches(107.8)
      )
    )

    val prePickupSecondCubePoint = Point(
      -Inches(46.4),
      Inches(36.4)
    )
    val pickupSecondCubePoint = Point(
      -Inches(26.5),
      Inches(60.6)
    )

    def dropOffFirstCube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLiftComp: CubeLiftComp
                        ): FiniteTask = {
      val pose = XYPosition.circularTracking(
        drivetrainHardware.turnPosition,
        drivetrainHardware.forwardPosition
      )

      new FollowWayPoints(
        leftCenterSwitchPoints,
        tolerance = Inches(6),
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .withTimeout(centetSwitchDriveTimeOut)
        .then(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
        )
    }


    def twoCubeCenterSwitch(drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLiftComp: CubeLiftComp
    ): FiniteTask = {
      val pose = XYPosition.circularTracking(
        drivetrainHardware.turnPosition,
        drivetrainHardware.forwardPosition
      )

     dropOffFirstCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLiftComp).then(
        new FollowWayPointsWithPosition(
          Seq(
            leftCenterSwitchPoints(leftCenterSwitchPoints.length - 1),
            leftCenterSwitchPoints(leftCenterSwitchPoints.length - 2),
            prePickupSecondCubePoint
          ),
          tolerance = Inches(6),
          maxTurnOutput = Percent(100),
          cruisingVelocity = purePursuitCruisingVelocity,
          targetTicksWithingTolerance = 1,
          forwardBackwardMode = BackwardsOnly,
          position = pose,
          turnPosition = drivetrainHardware.turnPosition
        )(drivetrain).then(
          new RotateToAngle(
            Degrees(30),
            Degrees(10)
          )(drivetrain)
        ).andUntilDone(
          liftElevatorToCollect(cubeLiftComp).toContinuous
        ).then(
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
            turnPosition = drivetrainHardware.turnPosition
          )(drivetrain).andUntilDone(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp)
          ).then(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLiftComp).forDuration(Seconds(1))
          ).then(
            new RotateToAngle(
              -Degrees(30),
              Degrees(10)
            )(drivetrain)
          ).then(
            new FollowWayPointsWithPosition(
              Seq(
                pickupSecondCubePoint,
                leftCenterSwitchPoints.last
              ),
              tolerance = Inches(6),
              maxTurnOutput = Percent(100),
              cruisingVelocity = purePursuitCruisingVelocity,
              targetTicksWithingTolerance = 1,
              forwardBackwardMode = ForwardsOnly,
              position = pose,
              turnPosition = drivetrainHardware.turnPosition
            )(drivetrain)
          )
        )
      )
    }

  }
}
