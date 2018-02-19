package com.lynbrookrobotics.eighteen.lift

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.commons.lift._
import com.lynbrookrobotics.potassium.frc.LazyTalon
import com.lynbrookrobotics.potassium.streams._
import squants.electro.ElectricPotential
import squants.{Dimensionless, Each}
import squants.space.Length

final case class CubeLiftHardware(talon: LazyTalon)(implicit coreTicks: Stream[Unit], props: CubeLiftProperties)
  extends LiftHardware {

  talon.t.configPeakOutputForward(props.maxMotorOutput.toEach, 0)
  talon.t.configPeakOutputReverse(-props.maxMotorOutput.toEach, 0)

  val nativeReading: Stream[Dimensionless] =
    coreTicks.map(_ => Each(talon.t.getSelectedSensorPosition(talon.idx)))

  override def position: Stream[Length] =
    nativeReading.map(props.fromNative)

  val voltage: Stream[ElectricPotential] = nativeReading.map(r => props.talonOverVoltage.recip * r)
}

object CubeLiftHardware {
  def apply(config: CubeLiftConfig, coreTicks: Stream[Unit]): CubeLiftHardware = {
    CubeLiftHardware(
      new LazyTalon(
        t = new TalonSRX(config.ports.motorPort),
        idx = 0,
        timeout = 0,
        defaultPeakOutputReverse = -1,
        defaultPeakOutputForward = +1
      )
    )(coreTicks, config.props)
  }
}
