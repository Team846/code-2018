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
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(Trigger) &&
        driverHardware.driverJoystick.getRawButton(TriggerBottom)
      }.foreach( // trigger & bottom trigger — [bottom trigger] + [trigger] + lift to collect height
        new WhileBelowPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight)
        )(lift).toContinuous
      )
    }

    for {
      rollers <- collectorRollers
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(Trigger)
      }.foreach( // trigger — close clamp, pivot down, spin rollers in
        CollectorTasks.collectCubeWithoutOpen(rollers, pivot)
      )
    }

    for {
      clamp <- collectorClamp
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(TriggerBottom)
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
        driverHardware.operatorJoystick.getRawButton(RightOne)
      }.foreach( // right 1 — lift to collect height
        new WhileAbovePosition(
          coreTicks.map(_ => cubeLiftProps.get.scaleHeight)
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightTwo)
      }.foreach( // right 2 — lift to switch height
        new WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.switchHeight),
          cubeLiftProps.get.switchTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightThree)
      }.foreach( // right 3 — lift to scale height
        new WhileBelowPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight)
        )(lift).toContinuous
      )
    }

    for {
      rollers <- collectorRollers
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(Trigger)
      }.foreach( // trigger — pivot down, spin rollers out
        CollectorTasks.purgeCube(rollers, pivot)
      )
    }

    for {
      clamp <- collectorClamp
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(TriggerBottom)
      }.foreach( // bottom trigger — open clamp, pivot down
        new OpenCollector(clamp) and new PivotDown(pivot)
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
      rollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightFour)
      }.foreach( // right 4 & joystick — manually control rollers
        new RollersManualControl(
          driverHardware.joystickStream.map(-_.operator.y)
        )(rollers)
      )
    }

    for {
      lift <- cubeLiftComp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightFive)
      }.foreach( // right 5 & joystick — manually control lift
        new LiftManualControl(
          driverHardware.joystickStream.map(-_.operator.y)
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
          driverHardware.joystickStream.map(-_.operator.y)
        )(winch)
      )
    }
  }
}
