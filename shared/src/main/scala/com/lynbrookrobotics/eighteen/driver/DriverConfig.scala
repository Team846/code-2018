package com.lynbrookrobotics.eighteen.driver

final case class DriverConfig(
  driverPort: Int,
  operatorPort: Int,
  driverWheelPort: Int,
  launchpadPort: Int
)
