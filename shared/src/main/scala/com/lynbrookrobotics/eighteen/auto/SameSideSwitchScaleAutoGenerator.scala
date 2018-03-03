package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.camera.CameraTasks
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.potassium.vision.limelight.LimeLightHardware
import squants.{Angle, Percent, Seconds}
import squants.space.{Degrees, Feet, Inches}

trait SameSideSwitchScaleAutoGenerator extends AutoGenerator {
  import  r._
  object SameSideSwitchAndScale {
    // vé al scale y caigas el cubo
    def startToScaleDropOff(drivetrain: DrivetrainComponent,
                            collectorRollers: CollectorRollers,
                            collectorClamp: CollectorClamp,
                            collectorPivot: CollectorPivot,
                            cubeLift: CubeLiftComp,
                            pose: Stream[Point],
                            relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.toScalePoints,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).and(liftElevatorToScale(cubeLift).toFinite).then(
        shootCubeScale(collectorRollers, collectorPivot, cubeLift)
      )
    }

    def backOutPostScale(drivetrain: DrivetrainComponent,
                         pose: Stream[Point],
                         relativeAngle: Stream[Angle]): FiniteTask = {
      new DriveDistance(
        -Feet(1),
        Inches(12)
      )(drivetrain).then(
        new RotateByAngle(
          -Degrees(135),
          Degrees(20),
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

    def pickupSecondCube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp,
                         limeLightHardware: LimeLightHardware,
                         pose: Stream[Point],
                         relativeAngle: Stream[Angle]): FiniteTask = {
      /*new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.pickupSecondCubePoints,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).andUntilDone(
        pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )*/CameraTasks.visionCubePickup(
        drivetrain,
        limeLightHardware,
        Feet(4),
        collectorRollers,
        collectorClamp,
        collectorPivot
      ).then(
        pickupGroundCube(
          collectorRollers,
          collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(0.5))
      ).then(
        new PivotDown(collectorPivot).forDuration(Seconds(1))
      )
    }

    def pickupThirdCube(drivetrain: DrivetrainComponent,
                        collectorRollers: CollectorRollers,
                        collectorClamp: CollectorClamp,
                        collectorPivot: CollectorPivot,
                        cubeLift: CubeLiftComp,
                        limeLightHardware: LimeLightHardware,
                        pose: Stream[Point],
                        relativeAngle: Stream[Angle]): FiniteTask = {
      /*new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.pickupThirdCubePoints,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).andUntilDone(
        pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )*/
      CameraTasks.visionCubePickup(
        drivetrain,
        limeLightHardware,
        Feet(4),
        collectorRollers,
        collectorClamp,
        collectorPivot
      ).then(
        pickupGroundCube(
          collectorRollers,
          collectorClamp, collectorPivot, cubeLift).forDuration(Seconds(1))
      )
    }

    def dropOffSecondCube(drivetrain: DrivetrainComponent,
                          collectorRollers: CollectorRollers,
                          collectorClamp: CollectorClamp,
                          collectorPivot: CollectorPivot,
                          cubeLift: CubeLiftComp,
                          pose: Stream[Point],
                          relativeAngle: Stream[Angle]): FiniteTask = {
      liftElevatorToSwitch(cubeLift).toFinite.then(new DriveBeyondStraight(
        Inches(6),
        Inches(1),
        Degrees(5),
        Percent(20)
      )(drivetrain)).then(
        dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      ).then(
        new DriveBeyondStraight(
          -Inches(18),
          Inches(1),
          Degrees(5),
          Percent(20)
        )(drivetrain)
      )
    }

    def backUpPreThirdCubeDropOff(drivetrain: DrivetrainComponent,
                                  collectorRollers: CollectorRollers,
                                  collectorClamp: CollectorClamp,
                                  pose: Stream[Point],
                                  relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.backupPreThirdCubeDropOffPoints,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain)
    }

    def dropOffThirdCube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp,
                         pose: Stream[Point],
                         relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchAndScalePoints.dropOffThirdCubePoints,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).and(liftElevatorToScale(cubeLift).toFinite).then(
        shootCubeScale(collectorRollers, collectorPivot, cubeLift)
      )
    }

    def scaleSwitch3Cube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp,
                         limeLightHardware: LimeLightHardware): FiniteTask = {
      val relativeAngle = drivetrainHardware.turnPosition.relativize((init, curr) => {
        curr - init
      })

      var iHateMyLife = false
      val pose = XYPosition
        .circularTracking(
          relativeAngle.map(compassToTrigonometric),
          drivetrainHardware.forwardPosition
        ).map(
        p => p + sideStartingPose
      ).preserve.withCheck(_ => {iHateMyLife = true})

      startToScaleDropOff(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle).then(
        backOutPostScale(drivetrain, pose, relativeAngle).and(liftElevatorToCollect(cubeLift).toFinite)
      ).then(
        pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, limeLightHardware, pose, relativeAngle)
      ).then(
        dropOffSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        pickupThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, limeLightHardware, pose, relativeAngle)
      ).then(
        backUpPreThirdCubeDropOff(drivetrain, collectorRollers, collectorClamp, pose, relativeAngle)
      ).then(
        dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      )
    }
  }
}
