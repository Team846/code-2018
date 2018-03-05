package com.lynbrookrobotics.eighteen.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDriveProperties
import com.lynbrookrobotics.potassium.control.offload.EscConfig
import com.lynbrookrobotics.potassium.units._
import squants.electro.ElectricCurrent
import squants.{Acceleration, Angle, Dimensionless, Each, Length, Time, Velocity}
import squants.motion.RadiansPerSecond
import squants.space.Turns
import squants.time.Seconds

final case class DrivetrainConfig(props: DrivetrainProperties, ports: DrivetrainPorts)

final case class DrivetrainProperties(
  maxLeftVelocity: Velocity,
  maxRightVelocity: Velocity,
  leftVelocityGains: ForwardVelocityGains,
  rightVelocityGains: ForwardVelocityGains,
  forwardPositionGains: ForwardPositionGains,
  turnVelocityGains: TurnVelocityGains,
  turnPositionGains: TurnPositionGains,
  maxAcceleration: Acceleration,
  maxDeceleration: Acceleration,
  maxCurrent: ElectricCurrent,
  defaultLookAheadDistance: Length,
  blendExponent: Double,
  track: Length,
  wheelDiameter: Length,
  wheelOverEncoderGears: Ratio[Angle, Angle],
  deltaVelocityStallThreshold: Velocity,
  stallTimeout: Time
) extends OffloadedDriveProperties {
  override val encoderAngleOverTicks: Ratio[Angle, Dimensionless] = Ratio(Turns(1), Each(4096))
  override val escConfig: EscConfig[Length] = EscConfig(
    ticksPerUnit = floorPerTick.recip
  )

  override val maxTurnVelocity = RadiansPerSecond(
    ((maxLeftVelocity + maxRightVelocity) * Seconds(1)) / track
  )
}

final case class DrivetrainPorts(
  practiceSpeedControllers: Boolean,
  leftPort: Int,
  rightPort: Int,
  leftFollowerPort: Int,
  rightFollowerPort: Int
)
