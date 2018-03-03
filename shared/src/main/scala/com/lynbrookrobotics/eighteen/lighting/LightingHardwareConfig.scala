package com.lynbrookrobotics.eighteen.lighting

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.LEDControllerConfig
import edu.wpi.first.wpilibj.DriverStation

final case class LightingHardwareConfig(alliance: Signal[DriverStation.Alliance], ledConfig: LEDControllerConfig)
