package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenCollector}
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, SpinForCollect}
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
      // go towards aisle
      startingPose,
      Point(
        -Inches(32.9),
        Inches(210.6)
      ),
      // cross aisle
      Point(
        -Inches(96.6),
        Inches(232.5) - Feet(1.25) + Inches(10)
      ),
      // drop cube to scale
      Point(
        -Inches(187) - Feet(2.5),
        Inches(232.5) - Feet(1.25) + Inches(10)
      ),
      Point(
        -Inches(222.4) - Inches(12),
        Inches(251.3)
      ),
      Point(
        -Inches(222.4) - Inches(12),
        Inches(275.3) - Inches(22)
      )
    )

    val pickupSecondCube = Seq(
      toScalePoints.last,
      toScalePoints.last - Point(
        Inches(0),
        Feet(1)
      ),
      Point(
        -Inches(226.7),
        Inches(222.6)
      )
    )

    val pickupThirdCube = Seq(
      toScalePoints.last,
      toScalePoints.last - Point(
        Inches(0),
        Feet(1)
      ),
      Point(
        -Inches(206.7),
        Inches(222.6)
      )
    )

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
        wayPoints = toScalePoints.map(invertXIfFromLeft),
        tolerance = Inches(3),
        maxTurnOutput = Percent(100),
        position = pose,
        turnPosition = relativeAngle,
        cruisingVelocity = FeetPerSecond(6),
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .then(
          new RotateByAngle(
            invertIfFromLeft(Degrees(20)),
            Degrees(10),
            1
          )(drivetrain)
            .withTimeout(Seconds(0.7))
            .andUntilDone(
              liftElevatorToScale(cubeLift).toContinuous
            ).then(
            shootCubeScale(collectorRollers, collectorPivot, cubeLift)
          ).andUntilDone(new PivotDown(collectorPivot))
        )
    }

    def spinAroundPostScale(drivetrain: DrivetrainComponent): FiniteTask = {
      new RotateByAngle(
        invertIfFromLeft(Degrees(180)),
        Degrees(5),
        1
      )(drivetrain)
    }

    def dropAdditionalCubeToScale(drivetrain: DrivetrainComponent,
                                  collectorRollers: CollectorRollers,
                                  collectorClamp: CollectorClamp,
                                  collectorPivot: CollectorPivot,
                                  cubeLift: CubeLiftComp,
                                  pose: Stream[Point],
                                  relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = toScalePoints.takeRight(3).map(invertXIfFromLeft),
        tolerance = Inches(6),
        maxTurnOutput = Percent(100),
        position = pose,
        turnPosition = relativeAngle,
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 2,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).withTimeout(Seconds(2)).and(
        liftElevatorToScale(cubeLift).toFinite
      ).then(
        new RotateByAngle(
          invertIfFromLeft(Degrees(25)),
          Degrees(10),
          1
        )(drivetrain).withTimeout(Seconds(2))
      ).then(
        shootCubeScale(collectorRollers, collectorPivot, cubeLift)
      ).andUntilDone(new PivotDown(collectorPivot))
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
          spinAroundPostScale(drivetrain)
            .withTimeout(Seconds(2))
            .andUntilDone(new WaitTask(Seconds(0.75)).then(liftElevatorToCollect(cubeLift).toContinuous))
            .andUntilDone(new PivotDown(collectorPivot))
        ).then(
          new FollowWayPointsWithPosition(
            wayPoints = pickupSecondCube.map(invertXIfFromLeft),
            tolerance = Inches(6),
            maxTurnOutput = Percent(100),
            position = pose,
            turnPosition = relativeAngle,
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly
          )(drivetrain).andUntilDone(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).and(
              new OpenCollector(collectorClamp)
            )
          ).then(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.5))
          )
        ).then(
          new RotateByAngle(
            invertIfFromLeft(Degrees(180)),
            Degrees(25),
            5
          )(drivetrain)
            .andUntilDone(new SpinForCollect(collectorRollers))
            .withTimeout(Seconds(2))
        ).then(
          dropAdditionalCubeToScale(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        ).then(
          spinAroundPostScale(drivetrain)
            .withTimeout(Seconds(2))
            .andUntilDone(new WaitTask(Seconds(0.75)).then(liftElevatorToCollect(cubeLift).toContinuous))
            .andUntilDone(new PivotDown(collectorPivot))
        ).then(
          new FollowWayPointsWithPosition(
            wayPoints = pickupThirdCube.map(invertXIfFromLeft),
            tolerance = Inches(6),
            maxTurnOutput = Percent(100),
            position = pose,
            turnPosition = relativeAngle,
            cruisingVelocity = purePursuitCruisingVelocity,
            targetTicksWithingTolerance = 1,
            forwardBackwardMode = ForwardsOnly
          )(drivetrain).andUntilDone(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).and(
              new OpenCollector(collectorClamp)
            )
          ).then(
            pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.5))
          )
        ).then(
          new RotateByAngle(
            invertIfFromLeft(-Degrees(180)),
            Degrees(25),
            5
          )(drivetrain)
            .andUntilDone(new SpinForCollect(collectorRollers))
            .withTimeout(Seconds(2))
        ).then(
          dropAdditionalCubeToScale(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        )
    }
  }
}
