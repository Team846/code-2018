package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.CoreRobot
import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.cubeLift.positionTasks._
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, WrapperTask}
import com.lynbrookrobotics.potassium.units.Point
import squants.motion.FeetPerSecond
import squants.space.Feet
import squants.time.{Milliseconds, Seconds}

class AutoGenerator(protected val r: CoreRobot) {
  import r._

  val purePursuitCruisingVelocity = FeetPerSecond(10)

  val robotLength = Feet(3)
  val robotWidth = Feet(3)

  val sideStartingPose = Point(-robotWidth / 2, robotLength / 2)

  val centerSwitchDriveTimeOut = Seconds(5)

  def printTask(message: String): FiniteTask = {
    new FiniteTask {
      override protected def onStart(): Unit = {
        println(message)
        finished()
      }

      override protected def onEnd(): Unit = {}
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
}
