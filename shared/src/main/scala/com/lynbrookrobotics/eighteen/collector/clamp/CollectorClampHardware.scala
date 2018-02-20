package com.lynbrookrobotics.eighteen.collector.clamp

import com.lynbrookrobotics.potassium.frc.ProximitySensor
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.wpilibj.Solenoid
import squants.electro.{ElectricPotential, Volts}

final case class CollectorClampHardware(solenoid: Solenoid, proximity: ProximitySensor, coreTicks: Stream[Unit]) {
  val proximitySensorReading: Stream[ElectricPotential] = coreTicks.map(_ => Volts(proximity.getVoltage))
}

object CollectorClampHardware {
  def apply(config: CollectorClampConfig, coreTicks: Stream[Unit]): CollectorClampHardware = {
    new CollectorClampHardware(
      new Solenoid(config.ports.pneumaticPort),
      new ProximitySensor(config.ports.proximityPort),
      coreTicks
    )
  }
}
