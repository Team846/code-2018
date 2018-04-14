package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.ForwardsOnly
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.potassium.units.Point
import squants.{Angle, Percent, Seconds}
import squants.space.{Degrees, Inches}

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

trait OppositeSwitchSameScaleGenerator extends AutoGenerator with SameSideScale {
  import r._

  object OppositeSideSwitchSameSideScale {
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
        wayPoints = OppositeSwitchPointsSameSideScale.pickupSecondCubePoints,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .andUntilDone(
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
        .then(
          new PivotDown(collectorPivot).forDuration(Seconds(1))
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
      new DriveDistanceStraight(
        Inches(3),
        Inches(1),
        Degrees(5),
        Percent(20)
      )(drivetrain)
        .then(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
        .then(
          new DriveDistanceStraight(
            -Inches(3),
            Inches(1),
            Degrees(5),
            Percent(20)
          )(drivetrain)
        )
    }

    def pickupThirdCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = OppositeSwitchPointsSameSideScale.pickupThirdCubePoints,
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

    def dropOffThirdCube(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorPivot: CollectorPivot,
      collectorClamp: CollectorClamp,
      cubeLift: CubeLiftComp
    ): FiniteTask = {
      new RotateToAngle(
        absoluteAngle = -Degrees(90) - Degrees(60),
        tolerance = Degrees(5)
      )(drivetrain).then(
        dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )
    }

    def scaleSwitch3CubeAuto(
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

      SameSideScale
        .startToScaleDropOff(
          drivetrain,
          collectorRollers,
          collectorClamp,
          collectorPivot,
          cubeLift,
          pose,
          relativeAngle
        )
        .withTimeout(Seconds(5))
        .then(
          SameSideScale
            .backOutPostScale(drivetrain, pose, relativeAngle)
            .and(liftElevatorToCollect(cubeLift).toFinite)
            .withTimeout(Seconds(3))
        )
        .then(
          pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(3))
        )
        .then(
          dropOffSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(5))
        )
        .then(
          pickupThirdCube(
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLift,
            pose,
            relativeAngle
          ).withTimeout(Seconds(2))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorPivot, collectorClamp, cubeLift)
        )
    }
  }
}
