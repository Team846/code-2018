package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, SpinForPurge, SpinForSlowPurge}
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.tasks.WaitTask
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.potassium.vision.limelight.LimeLightHardware
import squants.{Angle, Percent, Seconds}
import squants.space.{Degrees, Feet, Inches}

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
          new SpinForPurge(collectorRollers).forDuration(Seconds(3))
        )
        .andUntilDone(new PivotDown(collectorPivot))
    }

    def backOutPostScale(
      drivetrain: DrivetrainComponent,
      pose: Stream[Point],
      relativeAngle: Stream[Angle]
    ): FiniteTask = {
      new DriveDistance(
        -Feet(1),
        Inches(12)
      )(drivetrain).then(
        new RotateByAngle(
          -Degrees(155),
          Degrees(90),
          1
        )(drivetrain)
      )
      /*new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.backupPostScalePoints,
        tolerance = Feet(1.5),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly,
        angleDeadband = Degrees(20)
      )(drivetrain)*/
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
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.25))
        )
        .then(
          new PivotDown(collectorPivot).forDuration(Seconds(0.25))
        )
    }

    def pickupThirdCube(
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
        wayPoints = SameSideSwitchAndScalePoints.pickupThirdCubePoints,
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
          pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.25))
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
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.backupPreThirdCubeDropOffPoints,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain).and(new PivotDown(collectorPivot).forDuration(Seconds(0.5)))
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
        .then(
          shootCubeScale(collectorRollers, collectorPivot, cubeLift)
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

      SameSideSwitchAndScale.oneInScale(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, limeLightHardware)
          .then(
            new DriveBeyondStraight(
              -Feet(1),
              Inches(3),
              Degrees(5),
              Percent(50)
            )(drivetrain)
          )
        .then(
          pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, limeLightHardware, pose, relativeAngle)
            .withTimeout(Seconds(5))
        )
        .then(
          dropOffSecondCube(
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLift,
            pose,
            relativeAngle
          ).withTimeout(Seconds(4))
        ) /*
        .then(
          backUpPreThirdCubeDropOff(drivetrain, collectorRollers, collectorClamp, collectorPivot, pose, relativeAngle)
            .withTimeout(Seconds(3))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(3))
        )*/
    }

    def onlySwitch(
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
        .withTimeout(Seconds(5))
        .then(
          backOutPostScale(drivetrain, pose, relativeAngle)
            .and(liftElevatorToCollect(cubeLift).toFinite)
            .withTimeout(Seconds(3))
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
            .withTimeout(Seconds(3))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(3))
        )
        .then(
          pickupThirdCube(
            drivetrain,
            collectorRollers,
            collectorClamp,
            collectorPivot,
            cubeLift,
            limeLightHardware,
            pose,
            relativeAngle
          ).withTimeout(Seconds(4))
        )
        .then(
          backUpPreThirdCubeDropOff(drivetrain, collectorRollers, collectorClamp, collectorPivot, pose, relativeAngle)
            .withTimeout(Seconds(3))
        )
        .then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
            .withTimeout(Seconds(3))
        )
    }

  }
}
