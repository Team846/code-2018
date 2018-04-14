package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.ForwardsOnly
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.potassium.units.Point
import squants.{Angle, Percent, Seconds}
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import squants.motion.{FeetPerSecond, FeetPerSecondSquared}
import squants.space.{Degrees, Feet, Inches}

trait OppositeSideScale extends AutoGenerator {
  import r._

  object OppositeSideScale {
    import StartingPose._

    val toScalePoints = Seq(
      startingPose,
      Point(
        -Inches(32.9),
        Inches(210.6)
      ),
      Point(
        -Inches(96.6),
        Inches(232.5) - Feet(1.25)
      ),
      Point(
        -Inches(187) - Feet(1),
        Inches(232.5) - Feet(1.25)
      ),
      Point(
        -Inches(222.4) + Inches(6),
        Inches(251.3)
      ),
      Point(
        -Inches(222.4) + Inches(6),
        Inches(275.3) - Inches(12)
      )
    ).map(invertXIfFromLeft)

    def dropOffToScale(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      cubeLift: CubeLiftComp,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = toScalePoints,
        tolerance = Inches(3),
        maxTurnOutput = Percent(100),
        position = pose,
        turnPosition = relativeAngle,
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .then(
          new RotateToAngle(
            invertIfFromLeft(Degrees(30)),
            Degrees(5)
          )(drivetrain)
            .withTimeout(Seconds(0.7))
            .andUntilDone(
              liftElevatorToScale(cubeLift).toContinuous
            )
        )
        .then(
          shootCubeScale(collectorRollers, collectorPivot, cubeLift)
        )
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

      dropOffToScale(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        .then(
          new RotateToAngle(
            invertIfFromLeft(Degrees(180) - Degrees(10)),
            Degrees(5)
          )(drivetrain)
            .withTimeout(Seconds(2))
            .andUntilDone(new WaitTask(Seconds(0.5)).then(liftElevatorToCollect(cubeLift).toContinuous))
            .then(
              new DriveDistanceWithTrapezoidalProfile(
                cruisingVelocity = purePursuitCruisingVelocity,
                finalVelocity = FeetPerSecond(0),
                FeetPerSecondSquared(10),
                FeetPerSecondSquared(10),
                Inches(40),
                Inches(3),
                Degrees(3)
              )(drivetrain)
            )
            .andUntilDone(
              pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
            )
            .then(
              pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.5))
            )
        )
        .then(
          new DriveDistanceWithTrapezoidalProfile(
            cruisingVelocity = purePursuitCruisingVelocity,
            finalVelocity = FeetPerSecond(0),
            FeetPerSecondSquared(10),
            FeetPerSecondSquared(10),
            -Inches(20),
            Inches(3),
            Degrees(5)
          )(drivetrain)
            .then(
              new RotateToAngle(
                invertIfFromLeft(Degrees(10)),
                Degrees(3)
              )(drivetrain)
                .withTimeout(Seconds(1.5))
                .andUntilDone(
                  liftElevatorToScale(cubeLift).toContinuous
                )
            )
            .then(
              shootCubeScale(collectorRollers, collectorPivot, cubeLift)
            )
            .then(
              liftElevatorToCollect(cubeLift).toFinite
            )
        )
      //        .withTimeout(Seconds(10))
      //        .then(
      //          SameSideSwitchOppositeScale
      //            .pickupSecondCube(
      //              drivetrain,
      //              collectorRollers,
      //              collectorClamp,
      //              collectorPivot,
      //              cubeLift,
      //              pose,
      //              relativeAngle
      //            )
      //            .withTimeout(Seconds(5))
      //        )
      //        .then(
      //          SameSideSwitchOppositeScale
      //            .dropOffThirdCube(
      //              drivetrain,
      //              collectorRollers,
      //              collectorClamp,
      //              collectorPivot,
      //              cubeLift,
      //              pose,
      //              relativeAngle
      //            )
      //            .withTimeout(Seconds(5))
      //        )
      //        .then(
      //          SameSideSwitchOppositeScale
      //            .pickUpThirdCube(
      //              drivetrain,
      //              collectorRollers,
      //              collectorClamp,
      //              collectorPivot,
      //              cubeLift,
      //              pose,
      //              relativeAngle
      //            )
      //            .withTimeout(Seconds(5))
      //        )
      //        .then(
      //          SameSideSwitchOppositeScale
      //            .dropOffThirdCube(
      //              drivetrain,
      //              collectorRollers,
      //              collectorClamp,
      //              collectorPivot,
      //              cubeLift,
      //              pose,
      //              relativeAngle
      //            )
      //            .withTimeout(Seconds(5))
      //        )
    }

    val oppositeSideDriveTimeOut = Seconds(10)
  }
}
