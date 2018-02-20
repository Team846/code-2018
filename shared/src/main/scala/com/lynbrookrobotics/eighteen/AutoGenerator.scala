package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.potassium.vision.VisionTargetTracking
import com.lynbrookrobotics.potassium.vision.limelight.{LimeLightHardware, LimelightNetwork}
import squants.motion.FeetPerSecond
import squants.{Angle, Percent}
import squants.space.{Degrees, Feet, Inches, Length}
import squants.time.Seconds

class AutoGenerator(r: CoreRobot) {
  import r._

  def printTask(message: String): FiniteTask = {
    new FiniteTask {
      override protected def onEnd(): Unit = {}

      override protected def onStart(): Unit = {
        finished()
        println(message)
      }
    }
  }

  def driveBackPostSwitch(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
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
      new WaitTask(Seconds(2)).andUntilDone(
        CollectorTasks.purgeCube(collectorRollers)
      )
    )
  }

  def pickupCube(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
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
      CollectorTasks.collectCube(collectorRollers, collectorClamp)
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
      new WaitTask(Seconds(0.25)).andUntilDone(
        CollectorTasks.purgeCube(collectorRollers)
      )
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
      CollectorTasks.collectCube(collectorRollers, collectorClamp)
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

  def centerSwitch(drivetrain: DrivetrainComponent): FiniteTask = {
    new FollowWayPoints(
      Seq(
        Point.origin,
        Point(
          Inches(-55.393),
          Inches(111.993)
        ),
        Point(
          Inches(-55.393),
          Inches(143.993)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain)
  }

  val startingPose = Point(Inches(139.473), Inches(0))

  def twoCubeAutoWithPosition(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    xyPosition: Stream[Point],
    relativeAngle: Stream[Angle]
  ): FiniteTask = {
    new FollowWayPointsWithPosition(
      Seq(
        startingPose,
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
          xyPosition,
          relativeAngle
        ).then(printTask("ended post switch"))
      )
      .then(
        pickupCube(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeAngle).then(
          printTask("end cube pickup")
        )
      )
      .then(
        driveBackPostCube(drivetrain, xyPosition, relativeAngle)
          .then(printTask("end back driving"))
          .andUntilDone(
            CollectorTasks
              .collectCubeWithoutOpen(collectorRollers)
              .forDuration(Seconds(1))
              .toContinuous
          )
      )
      .then(
        driveToScaleForward(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeAngle).then(
          printTask("ended scale drop!")
        )
      )
  }

  def twoCubeAuto(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp
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
            p.x + startingPose.x,
            p.y + startingPose.y
        )
      )
      .preserve

    twoCubeAutoWithPosition(
      drivetrain,
      collectorRollers,
      collectorClamp,
      xyPosition,
      relativeTurn
    )
  }

  def threeCubeAuto(
    drivetrain: DrivetrainComponent,
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp
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
            p.x + startingPose.x,
            p.y + startingPose.y
        )
      )
      .preserve

    twoCubeAutoWithPosition(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn)
      .then(
        backOutAfterScale(drivetrain, xyPosition, relativeTurn).then(printTask("ended back out!"))
      )
      .then(
        pickupThirdCube(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn)
          .then(printTask("picked up third"))
      )
      .then(
        driveBackPostThirdCube(drivetrain, xyPosition, relativeTurn)
          .then(printTask("picked up third"))
          .andUntilDone(
            CollectorTasks
              .collectCubeWithoutOpen(collectorRollers)
              .forDuration(Seconds(1))
              .toContinuous
          )
      )
      .then(
        driveToScaleForward(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn)
          .then(printTask("ended scale drop and done!"))
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

  def visionCubePickup(drivetrain: DrivetrainComponent,
                       camera: LimeLightHardware,
                       minDistance: Length): FiniteTask = {

    new DriveToTargetWithConstantSpeed(drivetrain,
                                        camera.distanceToTarget,
                                        camera.angleToTarget,
                                        Percent(20),
                                        Percent(20),
                                        minDistance)
  }.then(printTask("done camera tracking"))
}
