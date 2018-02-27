package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.JoystickButtons._
import com.lynbrookrobotics.eighteen.climber.deployment.DeployClimber
import com.lynbrookrobotics.eighteen.climber.winch.{Climb, WinchManualControl}
import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.OpenCollector
import com.lynbrookrobotics.eighteen.collector.pivot.PivotDown
import com.lynbrookrobotics.eighteen.collector.rollers.RollersManualControl
import com.lynbrookrobotics.eighteen.cubeLift.LiftManualControl
import com.lynbrookrobotics.eighteen.cubeLift.positionTasks._
import com.lynbrookrobotics.eighteen.forklift.MoveForkliftDown


// https://team846.slab.com/posts/button-mappings-625f4bf7
object ButtonMappings {
  def setup(robot: CoreRobot): Unit = {
    import robot._

    // DRIVER
    for {
      lift <- cubeLiftComp
      pivot <- collectorPivot
      rollers <- collectorRollers
      clamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(Trigger) &&
        driverHardware.driverJoystick.getRawButton(TriggerBottom)
      }.foreach( // trigger & bottom trigger — [bottom trigger] + [trigger]
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous and CollectorTasks.collectCube(rollers, clamp, pivot)
      )
    }

    for {
      lift <- cubeLiftComp
      pivot <- collectorPivot
      rollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(Trigger) &&
        !driverHardware.driverJoystick.getRawButton(TriggerBottom)
      }.foreach( // trigger — pivot down, spin rollers in, lift to collect height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous and CollectorTasks.collectCubeWithoutOpen(rollers, pivot)
      )
    }

    for {
      clamp <- collectorClamp
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(TriggerBottom) &&
        !driverHardware.driverJoystick.getRawButton(Trigger)
      }.foreach( // bottom trigger — open clamp, pivot down
        new OpenCollector(clamp) and new PivotDown(pivot)
      )
    }

    for {
      climber <- climberWinch
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(LeftOne)
      }.foreach( // left 1 — run climber winch
        new Climb(climber)
      )
    }

    // OPERATOR
    for {
      lift <- cubeLiftComp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerBottom)
      }.foreach( // bottom trigger — lift to collect height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerLeft)
      }.foreach( // left trigger — lift to switch height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.switchHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerRight)
      }.foreach( // right trigger — lift to scale height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.lowScaleHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerRight)
      }.foreach( // right trigger — lift to scale height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.lowScaleHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightOne)
      }.foreach( // right trigger — lift to scale height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.highScaleHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(LeftSix)
      }.foreach( // right trigger — lift to scale height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.exchangeHeight),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )
    }

    for {
      rollers <- collectorRollers
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(Trigger)
      }.foreach( // trigger — pivot down, spin rollers out
        CollectorTasks.purgeCube(rollers, pivot)
      )
    }

    for {
      climber <- climberDeployment
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(LeftOne)
      }.foreach( // left 1 — deploy climber
        new DeployClimber(climber)
      )
    }

    for {
      forklift <- forklift
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(LeftTwo)
      }.foreach( // left 2 — deploy forklift
        new MoveForkliftDown(forklift)
      )
    }

    for {
      clamp <- collectorClamp
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(LeftThree)
      }.foreach( // left 3 — open clamp, pivot down
        new OpenCollector(clamp) and new PivotDown(pivot)
      )
    }

    for {
      rollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightFour)
      }.foreach( // right 4 & joystick — manually control rollers
        new RollersManualControl(
          driverHardware.joystickStream.map(-_.operator.y).syncTo(rollers.coreTicks)
        )(rollers)
      )
    }

    for {
      lift <- cubeLiftComp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightFive) &&
        !driverHardware.operatorJoystick.getRawButton(RightTwo)
      }.foreach( // right 5 & joystick — manually control lift
        new LiftManualControl(
          driverHardware.joystickStream.map(_.operator.y).syncTo(lift.coreTicks)
        )(lift)
      )
    }

    for {
      lift <- cubeLiftComp
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightTwo) &&
        driverHardware.operatorJoystick.getRawButton(RightFive)
      }.foreach( // right 2 — pivot down
        new PivotDown(pivot) and new LiftManualControl(
          driverHardware.joystickStream.map(_.operator.y).syncTo(lift.coreTicks)
        )(lift)
      )
    }

    for {
      winch <- climberWinch
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightSix)
      }.foreach( // right 6 & joystick — manually control winch
        new WinchManualControl(
          driverHardware.joystickStream.map(-_.operator.y).syncTo(winch.coreTicks)
        )(winch)
      )
    }

    for {
      clamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightThree)
      }.foreach( // right 1 — clamp open
        new OpenCollector(clamp)
      )
    }

    for {
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightTwo) &&
        !driverHardware.operatorJoystick.getRawButton(RightFive)
      }.foreach( // right 2 — pivot down
        new PivotDown(pivot)
      )
    }
  }
}
