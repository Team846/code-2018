package com.lynbrookrobotics.eighteen.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDriveProperties
import com.lynbrookrobotics.potassium.control.offload.EscConfig
import com.lynbrookrobotics.potassium.units._
import squants.electro.ElectricCurrent
import squants.{Acceleration, Angle, Dimensionless, Each, Length, Velocity}
import squants.motion.AngularVelocity
import squants.space.{Inches, Turns}

final case class DrivetrainConfig(props: DrivetrainProperties, ports: DrivetrainPorts)

final case class DrivetrainProperties(
  maxLeftVelocity: Velocity,
  maxRightVelocity: Velocity,
  leftVelocityGains: ForwardVelocityGains,
  rightVelocityGains: ForwardVelocityGains,
  forwardPositionGains: ForwardPositionGains,
  turnVelocityGains: TurnVelocityGains,
  turnPositionGains: TurnPositionGains,
  maxTurnVelocity: AngularVelocity,
  maxAcceleration: Acceleration,
  maxCurrent: ElectricCurrent,
  defaultLookAheadDistance: Length,
  blendExponent: Double,
  track: Length
) extends OffloadedDriveProperties {
  override val wheelDiameter: Length = Inches(4)
  override val wheelOverEncoderGears: Ratio[Angle, Angle] = Ratio(Turns(1), Turns(2))
  override val encoderAngleOverTicks: Ratio[Angle, Dimensionless] = Ratio(Turns(1), Each(4096))
  override val escConfig: EscConfig[Length] = EscConfig(
    ticksPerUnit = floorPerTick.recip
  )
}

final case class DrivetrainPorts(leftPort: Int, rightPort: Int, leftFollowerPort: Int, rightFollowerPort: Int)
