package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.CollectorTasks
import com.lynbrookrobotics.eighteen.collector.clamp.ClampCollector
import com.lynbrookrobotics.eighteen.collector.rollers.SpinForCollect

object ButtonMappings {
  def setup(robot: CoreRobot): Unit = {
    import robot._

    for {
      collectorRollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen(_ =>
        driverHardware.driverJoystick.getRawButton(1) &&
          !driverHardware.driverJoystick.getRawButton(2)).foreach(
        new SpinForCollect(collectorRollers)
      )
    }

    for {
      collectorClamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen(_ =>
        !driverHardware.driverJoystick.getRawButton(1) &&
          driverHardware.driverJoystick.getRawButton(2)).foreach(
        new ClampCollector(collectorClamp)
      )
    }

    for {
      collectorRollers <- collectorRollers
      collectorClamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen(_ =>
        driverHardware.driverJoystick.getRawButton(1) &&
          driverHardware.driverJoystick.getRawButton(2)).foreach(
        CollectorTasks.collectCubeClamped(collectorRollers, collectorClamp)
      )

      driverHardware.joystickStream.eventWhen(_ =>
        driverHardware.operatorJoystick.getRawButton(1)).foreach(
        CollectorTasks.purgeCubeClamped(collectorRollers, collectorClamp)
      )
    }
  }
}
