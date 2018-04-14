package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
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
import squants.space.{Degrees, Inches}

trait SameSideSwitchScaleAutoGenerator extends AutoGenerator {
  import r._
  object SameSideSwitchAndScale {
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

    def backOutPostScale(
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
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
        .then(
          pickupGroundCubeClosed(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.25))
        )
        .then(
          new PivotDown(collectorPivot).forDuration(Seconds(0.25))
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
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
        .then(
          pickupGroundCubeClosed(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.5))
        )
        .then(
          new PivotDown(collectorPivot).forDuration(Seconds(0.25))
        )
    }

    def pickupThirdCubeAfterSwitch(
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
        wayPoints = SameSideSwitchAndScalePoints.pickupThirdCubeAfterSwitchPoints,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .andUntilDone(
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
        .then(
          pickupGroundCubeClosed(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.25))
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
      liftElevatorToSwitch(cubeLift).toFinite
        .then(
          new DriveBeyondStraight(
            Inches(3),
            Inches(1),
            Degrees(5),
            Percent(20)
          )(drivetrain)
        )
        .andUntilDone(new PivotDown(collectorPivot))
        .then(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
        .then(
          new DriveBeyondStraight(
            -Inches(15),
            Inches(1),
            Degrees(5),
            Percent(20)
          )(drivetrain)
        )
    }

    def backUpPreThirdCubeDropOff(
      drivetrain: DrivetrainComponent,
      collectorRollers: CollectorRollers,
      collectorClamp: CollectorClamp,
      collectorPivot: CollectorPivot,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new RotateByAngle(
        Degrees(-180),
        Degrees(10),
        5
      )(drivetrain).andUntilDone(new SpinForCollect(collectorRollers)).withTimeout(Seconds(2))
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
          backOutPostScale(drivetrain, pose, relativeAngle)
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
          backUpPreThirdCubeDropOff(drivetrain, collectorRollers, collectorClamp, collectorPivot, pose, relativeAngle)
            .andUntilDone(new WaitTask(Seconds(0.5)).then(liftElevatorToCollect(cubeLift).toFinite).toContinuous)
            .withTimeout(Seconds(5))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(6))
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
