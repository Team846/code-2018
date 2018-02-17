package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.JoystickButtons._
import com.lynbrookrobotics.eighteen.climber.deployment.DeployClimber
import com.lynbrookrobotics.eighteen.climber.winch.Climb
import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.OpenCollector

// ### ALL CLIMBING IS ON THE LEFT ###
// ### ALL LIFTING IS ON THE RIGHT ###

object ButtonMappings {
  def setup(robot: CoreRobot): Unit = {
    import robot._

    // DRIVER
    for {
      lift <- cubeLiftComp
      rollers <- collectorRollers
      clamp <- collectorClamp
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(Trigger) && !driverHardware.driverJoystick.getRawButton(TriggerBottom)
      }.foreach( // trigger — lift go down, collect cube
        new cubeLift.positionTasks.WhileBelowPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight)
        )(lift).apply(
          CollectorTasks.collectCube(rollers, clamp, pivot)
        )
      )
    }

    if(cubeLiftComp.isEmpty) {
      for {
        rollers <- collectorRollers
        clamp <- collectorClamp
        pivot <- collectorPivot
      } {
        driverHardware.joystickStream.eventWhen { _ =>
          driverHardware.driverJoystick.getRawButton(Trigger) && !driverHardware.driverJoystick.getRawButton(TriggerBottom)
        }.foreach( // trigger — rip lift, collect cube
          CollectorTasks.collectCube(rollers, clamp, pivot)
        )
      }
    }

    for {
      clamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(TriggerBottom) && !driverHardware.driverJoystick.getRawButton(Trigger)
      }.foreach( // bottom trigger — open collector
        new OpenCollector(clamp)
      )
    }

    for {
      rollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(TriggerBottom) && !driverHardware.driverJoystick.getRawButton(Trigger)
      }.foreach( // trigger + bottom trigger — suck in cube (no open)
        CollectorTasks.collectCubeWithoutOpen(rollers)
      )
    }

    for {
      climber <- climberWinch
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(LeftOne)
      }.foreach( // left 1 — climb (winch)
        new Climb(climber)
      )
    }

    // OPERATOR
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
      lift <- cubeLiftComp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightOne)
      }.foreach( // right 1 — lift go scale height
        new cubeLift.positionTasks.WhileAbovePosition(
          coreTicks.map(_ => cubeLiftProps.get.scaleHeight)
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightTwo)
      }.foreach( // right 2 — lift go switch height
        new cubeLift.positionTasks.WhileAtPosition(
          coreTicks.map(_ => cubeLiftProps.get.switchHeight),
          cubeLiftProps.get.switchTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightThree)
      }.foreach( // right 3 —lift go collect height
        new cubeLift.positionTasks.WhileBelowPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight)
        )(lift).toContinuous
      )
    }

    for {
      rollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(Trigger)
      }.foreach( // trigger — eject cube
        CollectorTasks.purgeCube(rollers)
      )
    }
  }
}
