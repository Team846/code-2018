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
import com.lynbrookrobotics.eighteen.camera.CameraTasks.visionCubePickup
import squants.space.Feet

// team846.slab.com/posts/button-mappings-625f4bf7
object ButtonMappings {
  def setup(robot: CoreRobot): Unit = {
    import robot._

    for {
      lift <- cubeLiftComp
      pivot <- collectorPivot
      rollers <- collectorRollers
      clamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(Trigger) &&
        driverHardware.driverJoystick.getRawButton(TriggerBottom)
      }.foreach(
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
      }.foreach(
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
      }.foreach(
        new OpenCollector(clamp) and new PivotDown(pivot)
      )
    }

    for {
      climber <- climberWinch
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(LeftOne)
      }.foreach(
        new Climb(climber)
      )
    }

    for {
      camera <- cameraHardware
      drivetrainComponent <- drivetrain
      roller <- collectorRollers
      clamp <- collectorClamp
      pivot <- collectorPivot
      lift <- cubeLiftComp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(LeftSix)
      }.foreach(
        new WhileBelowPosition(
          coreTicks.map(_ => cubeLiftProps.get.collectHeight)
        )(lift).toContinuous
          and
        visionCubePickup(
            drivetrainComponent,
            camera,
            Feet(1.75),
            roller,
            clamp,
            pivot
          ).toContinuous
      )
    }

    for {
      lift <- cubeLiftComp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerBottom)
      }.foreach(
        new WhileAtPosition(
          driverHardware.joystickStream
            .map(_.operator.z)
            .map { twisty =>
              cubeLiftProps.get.collectHeight + (cubeLiftProps.get.twistyTotalRange * twisty.toEach)
            }
            .syncTo(coreTicks),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerLeft)
      }.foreach(
        new WhileAtPosition(
          driverHardware.joystickStream
            .map(_.operator.z)
            .map { twisty =>
              cubeLiftProps.get.switchHeight + (cubeLiftProps.get.twistyTotalRange * twisty.toEach)
            }
            .syncTo(coreTicks),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerRight)
      }.foreach(
        new WhileAtPosition(
          driverHardware.joystickStream
            .map(_.operator.z)
            .map { twisty =>
              cubeLiftProps.get.lowScaleHeight + (cubeLiftProps.get.twistyTotalRange * twisty.toEach)
            }
            .syncTo(coreTicks),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(TriggerRight)
      }.foreach(
        new WhileAtPosition(
          driverHardware.joystickStream
            .map(_.operator.z)
            .map { twisty =>
              cubeLiftProps.get.lowScaleHeight + (cubeLiftProps.get.twistyTotalRange * twisty.toEach)
            }
            .syncTo(coreTicks),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightOne)
      }.foreach(
        new WhileAtPosition(
          driverHardware.joystickStream
            .map(_.operator.z)
            .map { twisty =>
              cubeLiftProps.get.highScaleHeight + (cubeLiftProps.get.twistyTotalRange * twisty.toEach)
            }
            .syncTo(coreTicks),
          cubeLiftProps.get.liftPositionTolerance
        )(lift).toContinuous
      )

      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(LeftSix)
      }.foreach(
        new WhileAtPosition(
          driverHardware.joystickStream
            .map(_.operator.z)
            .map { twisty =>
              cubeLiftProps.get.exchangeHeight + (cubeLiftProps.get.twistyTotalRange * twisty.toEach)
            }
            .syncTo(coreTicks),
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
      }.foreach(
        CollectorTasks.purgeCube(rollers, pivot)
      )
    }

    for {
      climber <- climberDeployment
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(LeftOne)
      }.foreach(
        new DeployClimber(climber)
      )
    }

    for {
      forklift <- forklift
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(LeftTwo)
      }.foreach(
        new MoveForkliftDown(forklift)
      )
    }

    for {
      clamp <- collectorClamp
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(LeftThree)
      }.foreach(
        new OpenCollector(clamp) and new PivotDown(pivot)
      )
    }

    for {
      rollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightFour)
      }.foreach(
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
      }.foreach(
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
      }.foreach(
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
      }.foreach(
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
      }.foreach(
        new OpenCollector(clamp)
      )
    }

    for {
      pivot <- collectorPivot
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.operatorJoystick.getRawButton(RightTwo) &&
        !driverHardware.operatorJoystick.getRawButton(RightFive)
      }.foreach(
        new PivotDown(pivot)
      )
    }
  }
}
