package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.OpenCollector

object ButtonMappings {
  def setup(robot: CoreRobot): Unit = {
    import robot._

    for {
      collectorRollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(1) &&
        driverHardware.driverJoystick.getRawButton(2)
      }.foreach(
        CollectorTasks.collectCubeWithoutOpen(collectorRollers)
      )

      driverHardware.joystickStream
        .eventWhen(_ => driverHardware.operatorJoystick.getRawButton(1))
        .foreach(
          CollectorTasks.purgeCube(collectorRollers)
        )
    }

    for {
      collectorClamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        !driverHardware.driverJoystick.getRawButton(1) &&
        driverHardware.driverJoystick.getRawButton(2)
      }.foreach(
        new OpenCollector(collectorClamp)
      )
    }

    for {
      collectorRollers <- collectorRollers
      collectorClamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen { _ =>
        driverHardware.driverJoystick.getRawButton(1) &&
        !driverHardware.driverJoystick.getRawButton(2)
      }.foreach(
        CollectorTasks.collectCube(collectorRollers, collectorClamp)
      )
    }
  }
}
