package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.CoreRobot
import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
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
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Percent}

class AutoGenerator(protected val r: CoreRobot) {
  import r._

  val purePursuitCruisingVelocity = FeetPerSecond(10)

  val robotLength = Feet(3)
  val robotWidth = Feet(3)

  val sideStartingPose = Point(-robotWidth / 2, robotLength / 2)

  val liftUpMaxAcceleration = FeetPerSecondSquared(10)
  val liftDownMaxAcceleration = FeetPerSecondSquared(15)

  val driveBeyondFastLimit = Percent(50)
  val driveBeyondSlowLimit = Percent(10)

  val centerSwitchDriveTimeOut = Seconds(5)

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
      new PivotDown(collectorPivot)
        .forDuration(Milliseconds(500))
        .then(
          CollectorTasks
            .purgeCube(
              collectorRollers,
              collectorPivot
            )
            .forDuration(Seconds(1))
        )
    )
  }

  def dropCubeSwitch(
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLiftComp: CubeLiftComp
  ): FiniteTask = {
    liftElevatorToSwitch(cubeLiftComp).apply(
      CollectorTasks.purgeCube(collectorRollers, collectorPivot).forDuration(Seconds(0.25))
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
      CollectorTasks.collectCubeWithoutOpen(collectorRollers, collectorPivot)
    )
  }

  def pickupGroundCubeClosed(
    collectorRollers: CollectorRollers,
    collectorClamp: CollectorClamp,
    collectorPivot: CollectorPivot,
    cubeLift: CubeLiftComp
  ): ContinuousTask = {
    liftElevatorToCollect(cubeLift).toContinuous.and(
      CollectorTasks.collectCubeWithoutOpen(collectorRollers, collectorPivot)
    )
  }
}
