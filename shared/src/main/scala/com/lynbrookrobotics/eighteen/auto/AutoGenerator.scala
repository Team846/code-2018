package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.CoreRobot
import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
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
import squants.space.{Feet, Inches}
import squants.time.Seconds
import squants.{Angle, Percent}

class AutoGenerator(protected val r: CoreRobot) {
  import r._

  val purePursuitCruisingVelocity = FeetPerSecond(6)

  val robotLength = Feet(3)
  val robotWidth = Feet(3)

  val sideStartingPose = Point(-robotWidth / 2, robotLength / 2)

  val liftUpMaxAcceleration = FeetPerSecondSquared(10)
  val liftDownMaxAcceleration = FeetPerSecondSquared(15)

  val driveBeyondFastLimit = Percent(50)
  val driveBeyondSlowLimit = Percent(10)

  def printTask(message: String): FiniteTask = {
    new FiniteTask {
      override protected def onEnd(): Unit = {}

      override protected def onStart(): Unit = {
        finished()
        println(message)
      }
    }
  }

  def liftElevatorToScale(cubeLiftComp: CubeLiftComp): WrapperTask = {
    new WhileAtPosition(
      cubeLiftHardware.position.map(_ => cubeLiftProps.get.lowScaleHeight),
      cubeLiftProps.get.liftPositionTolerance
    )(cubeLiftComp)
  }

  def liftElevatorToSwitch(cubeLiftComp: CubeLiftComp): WrapperTask = {
    new WhileAtPosition(
      cubeLiftHardware.position.map(_ => cubeLiftProps.get.switchHeight),
      cubeLiftProps.get.liftPositionTolerance
    )(cubeLiftComp)
  }

  def shootCubeScale(
    collectorRollers: CollectorRollers,
    collectorPivot: CollectorPivot,
    cubeLiftComp: CubeLiftComp
  ): FiniteTask = {
    liftElevatorToScale(cubeLiftComp).apply(
      CollectorTasks
        .purgeCube(
          collectorRollers,
          collectorPivot
        )
        .forDuration(Seconds(0.25))
        .then(
          liftElevatorToCollect(cubeLiftComp).toFinite
        )
        .then(printTask("done lowering"))
    )
  }

  def dropCubeSwitch(
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLiftComp: CubeLiftComp
  ): FiniteTask = {
    liftElevatorToSwitch(cubeLiftComp).apply(
      CollectorTasks.purgeCubeOpen(collectorRollers, collectorClamp, collectorPivot).forDuration(Seconds(0.25))
    )
  }

  def liftElevatorToCollect(cubeLiftComp: CubeLiftComp): WrapperTask = {
    new WhileAtPosition(
      cubeLiftHardware.position.map(_ => cubeLiftProps.get.collectHeight),
      cubeLiftProps.get.liftPositionTolerance
    )(cubeLiftComp)
  }

  def pickupGroundCube(
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp
  ): ContinuousTask = {
    liftElevatorToCollect(cubeLift).toContinuous.and(
      CollectorTasks.collectCube(collectorRollers, collectorClamp, collectorPivot)
    )
  }

  def driveBackPostSwitch(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp,
    pose: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(72),
          Inches(140) - Feet(2) - Feet(1)
        ),
        Point( // become straight and move 32" forward
          Inches(72.313) - Feet(1),
          Inches(110.456) + Feet(1)
        ),
        Point(
          Inches(42.464) - Feet(1),
          Inches(110.456) + Feet(1)
        ),
        Point(
          Inches(42.464) - Feet(2) + Inches(6) + Inches(8),
          Inches(220.300) + Feet(2)
        )
      ),
      tolerance = Feet(1),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 1,
      forwardBackwardMode = BackwardsOnly,
      position = pose,
      turnPosition = relativeAngle
    )(drivetrain).and(
      shootCubeScale(collectorRollers, collectorPivot, cubeLift)
    )
  }

  def pickupCube(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp,
    position: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(42.5) - Feet(2) + Inches(6) + Inches(8),
          Inches(220.3) + Feet(2)
        ),
        Point(
          Inches(55.8) - Inches(4) + Inches(5) + Inches(4),
          Inches(208.7) + Feet(1) + Inches(3)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(8),
      targetTicksWithingTolerance = 1,
      forwardBackwardMode = ForwardsOnly,
      position = position,
      turnPosition = relativeAngle
    )(drivetrain).andUntilDone(
      pickupGroundCube(collectorRollers, collectorClamp, collectorPivot, cubeLift)
    )
  }

  def driveBackPostCube(
    drivetrain: DrivetrainComponent,
    pose: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(55.8) - Inches(4) + Inches(5),
          Inches(208.7) + Feet(1)
        ),
        Point(
          Inches(42.5),
          Inches(220.8)
        ),
        Point(
          Inches(0) + Feet(2.5),
          Inches(220.8)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 1,
      forwardBackwardMode = BackwardsOnly,
      position = pose,
      turnPosition = relativeAngle
    )(drivetrain)
  }

  def driveToScaleForward(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp,
    pose: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(0) + Feet(2.5),
          Inches(220.8)
        ),
        Point(
          Inches(50.3),
          Inches(299.6) - Feet(4) // only added to make it fit in the room
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 5,
      forwardBackwardMode = ForwardsOnly,
      position = pose,
      turnPosition = relativeAngle
    )(drivetrain).then(
      shootCubeScale(collectorRollers, collectorPivot, cubeLift)
    )
  }

  def backOutAfterScale(
    drivetrain: DrivetrainComponent,
    pose: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(50.3),
          Inches(299.6) - Feet(4) // only added to make it fit in the room
        ),
        Point(
          Inches(0) + Feet(3.5),
          Inches(299.6) - Feet(3.5)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 1,
      forwardBackwardMode = BackwardsOnly,
      position = pose,
      turnPosition = relativeAngle
    )(drivetrain)
  }

  def pickupThirdCube(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp,
    position: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(0) + Feet(3.5),
          Inches(299.6) - Feet(3.5)
        ),
        Point(
          Inches(52),
          Inches(299.6) - Feet(3.5)
        ),
        Point(
          Inches(55.8) + Feet(3),
          Inches(208.7) + Feet(1) + Inches(3)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(8),
      targetTicksWithingTolerance = 1,
      forwardBackwardMode = ForwardsOnly,
      position = position,
      turnPosition = relativeAngle
    )(drivetrain).andUntilDone(
      CollectorTasks.collectCube(collectorRollers, collectorClamp, collectorPivot)
    )
  }

  def driveBackPostThirdCube(
    drivetrain: DrivetrainComponent,
    pose: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(55.8) + Feet(3),
          Inches(208.7) + Feet(1)
        ),
        Point(
          Inches(42.5),
          Inches(220.8)
        ),
        Point(
          Inches(0) + Feet(3.5),
          Inches(220.8)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 1,
      forwardBackwardMode = BackwardsOnly,
      position = pose,
      turnPosition = relativeAngle
    )(drivetrain)
  }

  val centetSwitchDriveTimeOut = Seconds(5)
  def leftCenterSwitch(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLiftComp: CubeLiftComp
  ): FiniteTask = {
    new FollowWayPoints(
      Seq(
        Point.origin,
        Point(
          -Inches(61),
          Inches(72.7)
        ),
        Point(
          -Inches(61),
          Inches(107.8)
        )
      ),
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

  def rightCenterSwitch(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLiftComp: CubeLiftComp
  ): FiniteTask = {
    new FollowWayPoints(
      Seq(
        Point.origin,
        Point(
          Inches(38.5),
          Inches(66.6)
        ),
        Point(
          Inches(38.5),
          Inches(107.8)
        )
      ),
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

  val centerStartingPose = Point(Inches(139.473), Inches(0))

  def collectCubeDrivingBack(collectorRollers: CollectorRollers, collectorPivot: CollectorPivot): ContinuousTask = {
    CollectorTasks
      .collectCubeWithoutOpen(collectorRollers, collectorPivot)
      .forDuration(Seconds(1))
      .toContinuous
  }

  def twoCubeAutoWithPosition(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp,
    xyPosition: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        centerStartingPose,
        Point(
          Inches(72.313),
          Inches(97.786) - Feet(2) - Feet(1)
        ),
        Point( // become straight and move 32" forward
          Inches(72.313),
          Inches(140.188) - Feet(2) - Feet(1)
        )
      ),
      tolerance = Feet(1),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 1,
      forwardBackwardMode = ForwardsOnly,
      position = xyPosition,
      turnPosition = relativeAngle
    )(drivetrain)
      .then(printTask("ended switch"))
      .then(
        driveBackPostSwitch(
          drivetrain,
          collectorRollers,
          collectorClamp,
          collectorPivot,
          cubeLift,
          xyPosition,
          relativeAngle
        ).then(printTask("ended post switch"))
      )
      .then(
        pickupCube(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, xyPosition, relativeAngle)
          .then(
            printTask("end cube pickup")
          )
      )
      .then(
        driveBackPostCube(drivetrain, xyPosition, relativeAngle)
          .then(printTask("end back driving"))
          .andUntilDone(collectCubeDrivingBack(collectorRollers, collectorPivot))
      )
      .then(
        driveToScaleForward(
          drivetrain,
          collectorRollers,
          collectorClamp,
          collectorPivot,
          cubeLift,
          xyPosition,
          relativeAngle
        ).then(
          printTask("ended scale drop!")
        )
      )
  }

  def twoCubeAuto(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubelift: CubeLiftComp
  ): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition
      .circularTracking(
        relativeTurn.map(compassToTrigonometric),
        drivetrainHardware.forwardPosition
      )
      .map(
        p =>
          Point(
            p.x + centerStartingPose.x,
            p.y + centerStartingPose.y
        )
      )
      .preserve

    twoCubeAutoWithPosition(
      drivetrain,
      collectorRollers,
      collectorClamp,
      collectorPivot,
      cubelift,
      xyPosition,
      relativeTurn
    )
  }

  def threeCubeAuto(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp
  ): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition
      .circularTracking(
        relativeTurn.map(compassToTrigonometric),
        drivetrainHardware.forwardPosition
      )
      .map(
        p =>
          Point(
            p.x + centerStartingPose.x,
            p.y + centerStartingPose.y
        )
      )
      .preserve

    twoCubeAutoWithPosition(
      drivetrain,
      collectorRollers,
      collectorClamp,
      collectorPivot,
      cubeLift,
      xyPosition,
      relativeTurn
    ).then(
        backOutAfterScale(drivetrain, xyPosition, relativeTurn).then(printTask("ended back out!"))
      )
      .then(
        pickupThirdCube(
          drivetrain,
          collectorRollers,
          collectorClamp,
          collectorPivot,
          cubeLift,
          xyPosition,
          relativeTurn
        ).then(printTask("picked up third"))
      )
      .then(
        driveBackPostThirdCube(drivetrain, xyPosition, relativeTurn)
          .then(printTask("picked up third"))
          .andUntilDone(collectCubeDrivingBack(collectorRollers, collectorPivot))
      )
      .then(
        driveToScaleForward(
          drivetrain,
          collectorRollers,
          collectorClamp,
          collectorPivot,
          cubeLift,
          xyPosition,
          relativeTurn
        ).then(printTask("ended scale drop and done!"))
      )
  }

  def sameSideScaleAuto(drivetrain: DrivetrainComponent): FiniteTask = {
    new FollowWayPoints(
      Seq(
        Point.origin,
        Point( // turn 45 degrees counterclockwise and move 65.1" forward
          Inches(0),
          Inches(200) - Feet(5)
        ),
        Point(
          Inches(50) - Inches(20),
          Inches(200)
        ),
        Point( // turn 45 degrees clockwise and move 32" forward
          Inches(50),
          Inches(200)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain)
  }
}
