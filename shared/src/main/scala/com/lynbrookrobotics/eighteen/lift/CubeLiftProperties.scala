package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.commons.lift.LiftProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.units.{GenericIntegral, GenericValue}
import squants.Dimensionless
import squants.motion.Velocity
import squants.space.Length

case class CubeLiftProperties(PIDConfig: PIDConfig[Length, Length, GenericValue[Length], Velocity, GenericIntegral[Length], Dimensionless]) extends LiftProperties() {
  override def positionGains: PIDConfig[Length, Length, GenericValue[Length], Velocity, GenericIntegral[Length], Dimensionless] = PIDConfig
}
