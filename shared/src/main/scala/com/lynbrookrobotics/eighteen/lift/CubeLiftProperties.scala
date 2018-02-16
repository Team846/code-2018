package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.commons.lift.offloaded.OffloadedLiftProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.control.offload.EscConfig
import com.lynbrookrobotics.potassium.units.{GenericIntegral, GenericValue, _}
import squants.Dimensionless
import squants.electro.ElectricPotential
import squants.motion.Velocity
import squants.space.Length

case class CubeLiftProperties(
                               pidConfig: PIDConfig[Length,
                                 Length,
                                 GenericValue[Length],
                                 Velocity,
                                 GenericIntegral[Length],
                                 Dimensionless],
                               potentiometerConversion: Ratio[ElectricPotential, Length],
                               adc: Ratio[Dimensionless, ElectricPotential],
                               bottom: ElectricPotential
                             ) extends OffloadedLiftProperties() {
  override def positionGains: PIDConfig[Length, Length, GenericValue[Length], Velocity, GenericIntegral[Length], Dimensionless] = pidConfig

  override val escConfig: EscConfig[Length] = EscConfig(
    ticksPerUnit = adc * potentiometerConversion
  )

  override def toNative(height: Length): Dimensionless =
    adc * (potentiometerConversion * height + bottom)

  override def fromNative(native: Dimensionless): Length =
    potentiometerConversion.recip * (adc.recip * native - bottom)
}
