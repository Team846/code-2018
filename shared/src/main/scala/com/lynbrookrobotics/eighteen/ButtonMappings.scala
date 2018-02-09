package com.lynbrookrobotics.eighteen

object ButtonMappings {
  def setup(robot: CoreRobot): Unit = {
    import robot._

    for {
      collectorRollers <- collectorRollers
    } {
      driverHardware.joystickStream.eventWhen(_ =>
        driverHardware.driverJoystick.getRawButton(1) &&
          !driverHardware.driverJoystick.getRawButton(2)).foreach(
        new collector.CollectCubeOpen(collectorRollers)
      )
    }

    for {
      collectorClamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen(_ =>
        !driverHardware.driverJoystick.getRawButton(1) &&
          driverHardware.driverJoystick.getRawButton(2)).foreach(
        new collector.clamp.ClampCollector(collectorClamp)
      )
    }

    for {
      collectorRollers <- collectorRollers
      collectorClamp <- collectorClamp
    } {
      driverHardware.joystickStream.eventWhen(_ =>
        driverHardware.driverJoystick.getRawButton(1) &&
          driverHardware.driverJoystick.getRawButton(2)).foreach(
        new collector.CollectCubeClamped(collectorClamp, collectorRollers)
      )
    }
  }
}
