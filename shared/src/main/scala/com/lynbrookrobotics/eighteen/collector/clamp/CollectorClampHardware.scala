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
      {
        println(s"[DEBUG] Creating clamp solenoid on port ${config.ports.pneumaticPort}")
        new Solenoid(config.ports.pneumaticPort)
      },
      {
        println(s"[DEBUG] Creating clamp proximity sensor on port ${config.ports.proximityPort}")
        new ProximitySensor(config.ports.proximityPort)
      },
      coreTicks
    )
  }
}
