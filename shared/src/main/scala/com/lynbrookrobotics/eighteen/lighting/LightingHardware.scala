package com.lynbrookrobotics.eighteen.lighting

import com.lynbrookrobotics.potassium.frc.{LEDController, LEDControllerHardware}
import com.lynbrookrobotics.potassium.streams.Stream

class LightingHardware(coreTicks: Stream[Unit], config: LightingHardwareConfig) {
  val hardware = LEDControllerHardware(config.ledConfig)
  val controller = new LEDController(coreTicks, config.alliance)(hardware)
}
