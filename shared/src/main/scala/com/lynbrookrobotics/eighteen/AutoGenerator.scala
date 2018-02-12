package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenCollector}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, CollectorRollersProperties}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.eighteen.drivetrain.{DrivetrainComp => Drivetrain}
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.potassium.units.Point
import squants.motion.FeetPerSecond
import squants.{Angle, Percent}
import squants.space.{Feet, Inches}
import squants.time.Seconds

class AutoGenerator(r: CoreRobot) {
  import r._

  private val robotLength = Inches(35)

  private val gearPegDistance = Inches(109)

  def printTask(message: String): FiniteTask = {
    new FiniteTask {
      override protected def onEnd(): Unit = {}

      override protected def onStart(): Unit = {
        finished()
        println(message)
      }
    }
  }

  def driveBackPostSwitch(drivetrain: Drivetrain,
                          collectorRollers: CollectorRollers, collectorClamp: CollectorClamp,
                          pose: Stream[Point],
                          relativeAngle: Stream[Angle]): FiniteTask = {
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
      //postSwitchPoints,
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

  def pickupCube(drivetrain: Drivetrain,
                 collectorRollers: CollectorRollers, collectorClamp: CollectorClamp,
                 position: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
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

  def driveBackPostCube(drivetrain: Drivetrain, pose: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
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

  def driveToScaleForward(drivetrain: Drivetrain,
                          collectorRollers: CollectorRollers, collectorClamp: CollectorClamp,
                          pose: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
    new FollowWayPointsWithPosition(
      //driveForwardToScalePoints,
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

  def backOutAfterScale(drivetrain: Drivetrain,
                        pose: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
    new FollowWayPointsWithPosition(
      //driveForwardToScalePoints,
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

  def pickupThirdCube(drivetrain: Drivetrain,
                 collectorRollers: CollectorRollers, collectorClamp: CollectorClamp,
                 position: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
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

  def driveBackPostThirdCube(drivetrain: Drivetrain, pose: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
    new FollowWayPointsWithPosition(
      //driveBackToScalePoints,
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

  def centerSwitch(drivetrain: Drivetrain): FiniteTask = {
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

  def twoCubeAutoRelative(drivetrain: Drivetrain): FiniteTask = {
    new FollowWayPoints(
      Seq(
        startingPose,
        startingPose + Point(Feet(0), Feet(5))
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(100),
      cruisingVelocity = FeetPerSecond(20),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain)
  }

  val switchWayPoints = Seq(
    startingPose,
    startingPose + Point(-Feet(2), Feet(6)),
    startingPose + Point(-Feet(2), Feet(10))
  )

  val switchEnd = startingPose + Point(-Feet(2), Feet(10))
  val postSwitchPoints = Seq(
    switchEnd,
    switchEnd + Point(Feet(0), -Feet(4)),
    switchEnd + Point(-Feet(4), -Feet(4)),
    switchEnd + Point(-Feet(4), Feet(1))
  )

  val driveBackEnd = switchEnd + Point(-Feet(4), Feet(1))
  val cubePickupPoints = Seq(
    driveBackEnd,
    driveBackEnd + Point(Feet(2), -Feet(2))
  )

  val cubePickupEnd = driveBackEnd + Point(Feet(2), -Feet(2))
  val driveBackToScalePoints = Seq(
    cubePickupEnd,
    cubePickupEnd + Point(-Feet(2), Feet(1)),
    cubePickupEnd + Point(-Feet(4), Feet(1))
  )

  val driveBackToScaleEnd = cubePickupEnd + Point(-Feet(4), Feet(1))
  val driveForwardToScalePoints = Seq(
    driveBackToScaleEnd,
    driveBackToScaleEnd + Point(Feet(2), Feet(6))
  )


  def twoCubeAuto(drivetrain: Drivetrain, collectorRollers: CollectorRollers, collectorClamp: CollectorClamp): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition.circularTracking(
      relativeTurn.map(compassToTrigonometric),
      drivetrainHardware.forwardPosition
    ).map(p =>
      Point(
        p.x + startingPose.x,
        p.y + startingPose.y
      )
    ).withCheck(println).preserve

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
      turnPosition = relativeTurn
    )(drivetrain).then(printTask("ended switch")).then(
      driveBackPostSwitch(
        drivetrain, collectorRollers, collectorClamp,
        xyPosition, relativeTurn
      ).then(printTask("ended post switch"))
    ).then(
      pickupCube(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn).then(printTask("end cube pickup"))
    ).then(
      driveBackPostCube(drivetrain, xyPosition, relativeTurn).then(printTask("end back driving"))
    ).then(
      driveToScaleForward(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn).then(printTask("ended everything!"))
    )
  }

  def threeCubeAuto(drivetrain: Drivetrain, collectorRollers: CollectorRollers, collectorClamp: CollectorClamp): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition.circularTracking(
      relativeTurn.map(compassToTrigonometric),
      drivetrainHardware.forwardPosition
    ).map(p =>
      Point(
        p.x + startingPose.x,
        p.y + startingPose.y
      )
    ).withCheck(println).preserve

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
      turnPosition = relativeTurn
    )(drivetrain).then(printTask("ended switch")).then(
      driveBackPostSwitch(
        drivetrain, collectorRollers, collectorClamp,
        xyPosition, relativeTurn
      ).then(printTask("ended post switch"))
    ).then(
      pickupCube(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn).then(printTask("end cube pickup"))
    ).then(
      driveBackPostCube(drivetrain, xyPosition, relativeTurn).then(printTask("end back driving")).andUntilDone(
        new WaitTask(Seconds(1)).andUntilDone(
          CollectorTasks.collectCubeWithoutOpen(collectorRollers)
        ).toContinuous
      )
    ).then(
      driveToScaleForward(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn).then(printTask("ended scale drop!"))
    ).then(
      backOutAfterScale(drivetrain, xyPosition, relativeTurn).then(printTask("ended back out!"))
    ).then(
      pickupThirdCube(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn).then(printTask("picked up third"))
    ).then(
      driveBackPostThirdCube(drivetrain, xyPosition, relativeTurn).then(printTask("picked up third")).andUntilDone(
        new WaitTask(Seconds(1)).andUntilDone(
          CollectorTasks.collectCubeWithoutOpen(collectorRollers)
        ).toContinuous
      )
    ).then(
      driveToScaleForward(drivetrain, collectorRollers, collectorClamp, xyPosition, relativeTurn).then(printTask("ended scale drop and done!"))
    )
  }

  def sameSideScaleAuto(drivetrain: Drivetrain): FiniteTask = {
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