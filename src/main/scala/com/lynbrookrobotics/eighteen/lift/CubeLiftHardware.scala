package com.lynbrookrobotics.eighteen.lift

import com.ctre.phoenix.motorcontrol.can.{TalonSRX, VictorSPX}
import com.lynbrookrobotics.potassium.commons.lift
import edu.wpi.first.wpilibj.{AnalogInput, Spark}
import edu.wpi.first.wpilibj.interfaces.Potentiometer

case class LiftHardware(potentiometer: AnalogInput, spark: Spark)

object LiftHardware {
  def apply(config: LiftConfig): LiftHardware = {
    LiftHardware(
      new AnalogInput(config.potentiometerPort),
      new Spark(config.motorPort)
    )
  }
}

