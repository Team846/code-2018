package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.commons.lift.offloaded.OffloadedLiftProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.control.offload.EscConfig
import com.lynbrookrobotics.potassium.units._
import squants.Dimensionless
import squants.electro.ElectricPotential
import squants.motion.Velocity
import squants.space.Length

final case class CubeLiftProperties(
  pidConfig: PIDConfig[Length, Length, GenericValue[Length], Velocity, GenericIntegral[Length], Dimensionless],
  voltageOverHeight: Ratio[ElectricPotential, Length],
  talonOverVoltage: Ratio[Dimensionless, ElectricPotential],
  voltageAtBottom: ElectricPotential,
  collectHeight: Length,
  switchHeight: Length,
  scaleHeight: Length,
  switchTolerance: Length,
  maxManualControlOutput: Dimensionless
) extends OffloadedLiftProperties {
  override def positionGains: PIDConfig[
    Length,
    Length,
    GenericValue[Length],
    Velocity,
    GenericIntegral[Length],
    Dimensionless
  ] = pidConfig

  override val escConfig: EscConfig[Length] = EscConfig(
    ticksPerUnit = talonOverVoltage * voltageOverHeight
  )

  override def toNative(height: Length): Dimensionless =
    talonOverVoltage * (voltageOverHeight * height + voltageAtBottom)

  override def fromNative(native: Dimensionless): Length =
    voltageOverHeight.recip * (talonOverVoltage.recip * native - voltageAtBottom)
}
