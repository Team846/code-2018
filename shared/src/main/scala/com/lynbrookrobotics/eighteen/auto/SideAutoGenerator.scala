package com.lynbrookrobotics.eighteen.auto
import com.lynbrookrobotics.eighteen.CoreRobot
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.units.Point
import squants.motion.FeetPerSecondSquared
import squants.space.{Degrees, Feet, Inches}
import squants.{Angle, Percent}


trait SideAutoGenerator extends AutoGenerator {
  import r._

  val liftUpMaxAcceleration = FeetPerSecondSquared(10)
  val liftDownMaxAcceleration = FeetPerSecondSquared(15)

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
        )(drivetrain).then(
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
                         pose: Stream[Point],
                         relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
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
      )
    }

    def pickupThirdCube(drivetrain: DrivetrainComponent,
                        collectorRollers: CollectorRollers,
                        collectorClamp: CollectorClamp,
                        collectorPivot: CollectorPivot,
                        cubeLift: CubeLiftComp,
                        pose: Stream[Point],
                        relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
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
      )
    }

    def dropOffSecondCube(drivetrain: DrivetrainComponent,
                          collectorRollers: CollectorRollers,
                          collectorClamp: CollectorClamp,
                          collectorPivot: CollectorPivot,
                          cubeLift: CubeLiftComp,
                          pose: Stream[Point],
                          relativeAngle: Stream[Angle]): FiniteTask = {
     new DriveBeyondStraight(
       Inches(18),
       Inches(1),
       Degrees(5),
       Percent(20)
     )(drivetrain).then(
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
      )(drivetrain).then(
        shootCubeScale(collectorRollers, collectorPivot, cubeLift)
      )
    }

    def scaleSwitch3Cube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp): FiniteTask = {
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
        backOutPostScale(drivetrain, pose, relativeAngle)
      ).then(
        pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        dropOffSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        pickupThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        backUpPreThirdCubeDropOff(drivetrain, collectorRollers, collectorClamp, pose, relativeAngle)
      ).then(
        dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      )
    }
  }

  object OppositeSideSwitchSameSideScale {
    def pickupSecondCube(drivetrain: DrivetrainComponent,
                        collectorRollers: CollectorRollers,
                        collectorClamp: CollectorClamp,
                        collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp,
                        pose: Stream[Point],
                        relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = OppositeSwitchPointsSameSideScale.pickupSecondCubePoints,
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

    def dropOffSecondCube(drivetrain: DrivetrainComponent,
                          collectorRollers: CollectorRollers,
                          collectorClamp: CollectorClamp,
                          collectorPivot: CollectorPivot,
                          cubeLift: CubeLiftComp,
                          pose: Stream[Point],
                          relativeAngle: Stream[Angle]): FiniteTask = {
      new DriveBeyondStraight(
        Inches(6),
        Inches(1),
        Degrees(5),
        Percent(20)
      )(drivetrain).then(
        dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      ).then(
        new DriveBeyondStraight(
          -Inches(6),
          Inches(1),
          Degrees(5),
          Percent(20)
        )(drivetrain)
      )
    }

    def pickupThirdCube(drivetrain: DrivetrainComponent,
                        collectorRollers: CollectorRollers,
                        collectorClamp: CollectorClamp,
                        collectorPivot: CollectorPivot,
                        cubeLift: CubeLiftComp,
                        pose: Stream[Point],
                        relativeAngle: Stream[Angle]): FiniteTask = {
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

    def dropOffThirdCube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorPivot: CollectorPivot,
                         collectorClamp: CollectorClamp,
                         cubeLift: CubeLiftComp): FiniteTask = {
      new RotateToAngle(
        absoluteAngle = -Degrees(90) - Degrees(60),
        tolerance = Degrees(5)
      )(drivetrain).then(
        dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )
    }

    def scaleSwitch3CubeAuto(drivetrain: DrivetrainComponent,
                             collectorRollers: CollectorRollers,
                             collectorClamp: CollectorClamp,
                             collectorPivot: CollectorPivot,
                             cubeLift: CubeLiftComp): FiniteTask = {
      val relativeAngle = drivetrainHardware.turnPosition.relativize((init, curr) => {
        curr - init
      })

      val pose = XYPosition
        .circularTracking(
          relativeAngle.map(compassToTrigonometric),
          drivetrainHardware.forwardPosition
        ).map(
        p => p + sideStartingPose
      ).preserve

      SameSideSwitchAndScale.startToScaleDropOff(
        drivetrain,
        collectorRollers,
        collectorClamp,
        collectorPivot,
        cubeLift,
        pose,
        relativeAngle
      ).then(
        SameSideSwitchAndScale.backOutPostScale(drivetrain, pose, relativeAngle)
      ).then(
        pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        dropOffSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        printTask("before third pickup").then(
          pickupThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        ).then(printTask("after pickup second cube"))
      ).then(
        printTask("before drop off third").then(
          dropOffThirdCube(drivetrain, collectorRollers, collectorPivot, collectorClamp, cubeLift)
        ).then(printTask("after pickup third cube"))
      )
    }
  }

  object OppositeSideSwitchAndScale {
    def dropOffToScale(drivetrain: DrivetrainComponent,
                       collectorRollers: CollectorRollers,
                       collectorClamp: CollectorClamp,
                       collectorPivot: CollectorPivot,
                       cubeLift: CubeLiftComp,
                       pose: Stream[Point],
                       relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = OppositeSideSwitchScalePoints.toScalePoints,
        tolerance = Inches(3),
        maxTurnOutput = Percent(100),
        position = pose,
        turnPosition = relativeAngle,
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).then(
        shootCubeScale(collectorRollers, collectorPivot, cubeLift)
      )
    }

    def scaleSwitch3CubeAuto(drivetrain: DrivetrainComponent,
                             collectorRollers: CollectorRollers,
                             collectorClamp: CollectorClamp,
                             collectorPivot: CollectorPivot,
                             cubeLift: CubeLiftComp): FiniteTask = {
      val relativeAngle = drivetrainHardware.turnPosition.relativize((init, curr) => {
        curr - init
      })

      val pose = XYPosition.circularTracking(
          relativeAngle.map(compassToTrigonometric),
          drivetrainHardware.forwardPosition
        ).map(
        p => p + sideStartingPose
      ).preserve


      dropOffToScale(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle).then(
        SameSideSwitchOppositeScale.pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        SameSideSwitchOppositeScale.dropOffSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        SameSideSwitchOppositeScale.pickUpThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        SameSideSwitchOppositeScale.dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      )
    }
  }

  val driveBeyondFastLimit = Percent(50)
  val driveBeyondSlowLimit = Percent(10)

  object SameSideSwitchOppositeScale {
    val prePickupPoint = Point(
      -Inches(264.0),
      Inches(232.3)
    )

    def dropOffToSwitch(drivetrain: DrivetrainComponent,
                        collectorRollers: CollectorRollers,
                        collectorClamp: CollectorClamp,
                        collectorPivot: CollectorPivot,
                        cubeLift: CubeLiftComp,
                        pose: Stream[Point],
                        relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.toSwitchPoints,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain).then(
        dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )
    }

    def driveBackPostSwitch(drivetrain: DrivetrainComponent,
                            collectorRollers: CollectorRollers,
                            collectorClamp: CollectorClamp,
                            pose: Stream[Point],
                            relativeAngle: Stream[Angle]): FiniteTask = {
      new DriveBeyondStraight(
        -Inches(12),
        toleranceForward = Inches(1),
        Degrees(5),
        Percent(50))(drivetrain).then(
      new FollowWayPointsWithPosition(
          wayPoints = SameSideSwitchOppositeScalePoints.driveBackPostSwitch,
          tolerance = Inches(3),
          position = pose,
          turnPosition = relativeAngle,
          maxTurnOutput = Percent(100),
          cruisingVelocity = purePursuitCruisingVelocity,
          targetTicksWithingTolerance = 10,
          forwardBackwardMode = BackwardsOnly
        )(drivetrain)
      )
    }

    def pickupSecondCube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp,
                         pose: Stream[Point],
                         relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.pickupSecondCube,
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

    def pickUpThirdCube(drivetrain: DrivetrainComponent,
                        collectorRollers: CollectorRollers,
                        collectorClamp: CollectorClamp,
                        collectorPivot: CollectorPivot,
                        cubeLift: CubeLiftComp,
                        pose: Stream[Point],
                        relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.backUpToPickupThirdCube,
        tolerance = Inches(3),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain).then(
        new FollowWayPointsWithPosition(
          wayPoints = SameSideSwitchOppositeScalePoints.pickupThirdCube,
          tolerance = Inches(3),
          position = pose,
          turnPosition = relativeAngle,
          maxTurnOutput = Percent(100),
          cruisingVelocity = purePursuitCruisingVelocity,
          targetTicksWithingTolerance = 10,
          forwardBackwardMode = ForwardsOnly
      )(drivetrain)).andUntilDone(
        pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )
    }

    def dropOffSecondCube(drivetrain: DrivetrainComponent,
                          collectorRollers: CollectorRollers,
                          collectorClamp: CollectorClamp,
                          collectorPivot: CollectorPivot,
                          cubeLift: CubeLiftComp,
                          pose: Stream[Point],
                          relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.backUpToDropOffSecondCube,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain).andUntilDone(
        collectCubeDrivingBack(collectorRollers, collectorPivot)
      ).then(
        new FollowWayPointsWithPosition(
          wayPoints = SameSideSwitchOppositeScalePoints.forwardsDropOffSecondCube,
          tolerance = Inches(6),
          position = pose,
          turnPosition = relativeAngle,
          maxTurnOutput = Percent(100),
          cruisingVelocity = purePursuitCruisingVelocity,
          targetTicksWithingTolerance = 1,
          forwardBackwardMode = ForwardsOnly
      )(drivetrain)).then(
        dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
      )
    }

    def dropOffThirdCube(drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp,
                         pose: Stream[Point],
                         relativeAngle: Stream[Angle]): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.backUpToDropOffThirdCube,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 1,
        forwardBackwardMode = BackwardsOnly
      )(drivetrain).andUntilDone(
        collectCubeDrivingBack(collectorRollers, collectorPivot)
      ).then(
        new FollowWayPointsWithPosition(
          wayPoints = SameSideSwitchOppositeScalePoints.forwardsDropOffThirdCube,
          tolerance = Inches(6),
          position = pose,
          turnPosition = relativeAngle,
          maxTurnOutput = Percent(100),
          cruisingVelocity = purePursuitCruisingVelocity,
          targetTicksWithingTolerance = 1,
          forwardBackwardMode = ForwardsOnly
        )(drivetrain)
      ).then(
        shootCubeScale(collectorRollers, collectorPivot, cubeLift)
      )
    }


    def scaleSwitch3CubeAuto(drivetrain: DrivetrainComponent,
                             collectorRollers: CollectorRollers,
                             collectorClamp: CollectorClamp,
                             collectorPivot: CollectorPivot,
                             cubeLift: CubeLiftComp): FiniteTask = {
      val relativeAngle = drivetrainHardware.turnPosition.relativize((init, curr) => {
        curr - init
      })

      val pose = XYPosition.circularTracking(
          relativeAngle.map(compassToTrigonometric),
          drivetrainHardware.forwardPosition
        ).map(
        p => p + sideStartingPose
      ).preserve

      dropOffToSwitch(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle).then(
        driveBackPostSwitch(drivetrain, collectorRollers, collectorClamp, pose, relativeAngle)
      ).then(
        pickupSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        dropOffSecondCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        pickUpThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      ).then(
        dropOffThirdCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
      )
    }
  }
}

class FullAutoGenerator(r: CoreRobot) extends AutoGenerator(r) with SideAutoGenerator
