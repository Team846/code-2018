package com.lynbrookrobotics.eighteen.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.{OffloadedDriveProperties}
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveProperties
import com.lynbrookrobotics.potassium.control.offload.{EscConfig, OffloadedSignal}
import com.lynbrookrobotics.potassium.units.Ratio
import squants.motion.{Acceleration, RadiansPerSecond, Velocity}
import squants.space.{Degrees, Inches, Radians}
import squants.time.Seconds
import squants.{Angle, Dimensionless, Each, Length, Percent}

case class DrivetrainProperties(maxLeftVelocity: Velocity, maxRightVelocity: Velocity,
                                maxAcceleration: Acceleration,
                                wheelDiameter: Length, track: Length, gearRatio: Double,
                                turnVelocityGains: TurnVelocityGains,
                                forwardPositionGains: ForwardPositionGains,
                                turnPositionGains: TurnPositionGains,
                                leftVelocityGains: ForwardVelocityGains,
                                rightVelocityGains: ForwardVelocityGains,
                                currentLimit: Dimensionless,
                                defaultLookAheadDistance: Length,
                                blendExponent: Double,
                                robotLength: Length) extends TwoSidedDriveProperties with OffloadedDriveProperties {
  override val maxTurnVelocity = RadiansPerSecond((((maxLeftVelocity + maxRightVelocity) * Seconds(1)) / Inches(21.75)) / 2)

  val maxCurvature = Ratio(
    num = Each(Int.MaxValue),
    den = track / 2d)

  override val wheelOverEncoderGears: Ratio[Angle, Angle] = Ratio(Radians(gearRatio), Radians(1))
  override val encoderAngleOverTicks: Ratio[Angle, Dimensionless] = Ratio(Degrees(360), Each(8192))

  override val escConfig: EscConfig[Length] = EscConfig(ticksPerUnit = floorPerTick.recip)
}
