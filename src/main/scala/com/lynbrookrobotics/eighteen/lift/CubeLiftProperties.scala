package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.commons.lift.LiftProperties
import com.lynbrookrobotics.potassium.commons.lift.offloaded.OffloadedLiftProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.control.offload.EscConfig
import com.lynbrookrobotics.potassium.units.{GenericIntegral, GenericValue}
import squants.Dimensionless
import squants.motion.Velocity
import squants.space.{Inches, Length}

case class CubeLiftProperties(PIDConfig: PIDConfig[Length, Length, GenericValue[Length], Velocity, GenericIntegral[Length], Dimensionless]) extends OffloadedLiftProperties() {
  override def positionGains: PIDConfig[Length, Length, GenericValue[Length], Velocity, GenericIntegral[Length], Dimensionless] = PIDConfig

  override val escConfig: EscConfig[Length] = _

  override def toNative(height: Length): Dimensionless = ???

  override def fromNative(native: Dimensionless): Length = {
    Inches(1)
  }
}
