package com.lynbrookrobotics.eighteen.lift

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.commons.lift._
import com.lynbrookrobotics.potassium.frc.LazyTalon
import squants.Each
import squants.space.Length

final case class CubeLiftHardware(talon: LazyTalon)(
  implicit coreTicks: Stream[Unit],
  props: CubeLiftProperties
) extends LiftHardware {

  override def position: Stream[Length] =
    coreTicks.map(_ => props.fromNative(Each(talon.t.getSelectedSensorPosition(talon.idx))))
}

object CubeLiftHardware {
  def apply(config: CubeLiftConfig)(implicit coreTicks: Stream[Unit]): CubeLiftHardware =
    CubeLiftHardware(
      new LazyTalon(
        new TalonSRX(config.ports.motorPort),
        idx = config.idx,
        timeout = config.timeout,
        -1,
        +1
      )
    )(coreTicks, config.props)
}
