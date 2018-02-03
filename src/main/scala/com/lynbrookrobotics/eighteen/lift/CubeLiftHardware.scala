package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.streams
import com.lynbrookrobotics.potassium.commons.lift._
import com.lynbrookrobotics.potassium.units.Ratio
import edu.wpi.first.wpilibj.{AnalogInput, Spark}
import squants.{Dimensionless, Each, Time}
import squants.space.{Inches, Length}
import squants.time.Milliseconds

case class CubeLiftHardware(potentiometer: AnalogInput,
                            spark: Spark,
                            period: Time) (implicit clock: Clock) extends LiftHardware {
  val denom: Dimensionless = ???
  val lengthToPotCF: Ratio[Length, Dimensionless] = Ratio(Inches(1), denom)

  def getLength: Length = lengthToPotCF * Each(potentiometer.getAverageValue.toDouble)

  override def position: Stream[Length] = Stream.periodic(period)(
    // TODO: Fix
    getLength
  )
}

object CubeLiftHardware {
  def apply(config: CubeLiftConfig)(implicit clock: Clock): CubeLiftHardware = {
    CubeLiftHardware(
      new AnalogInput(config.ports.potentiometerPort),
      new Spark(config.ports.motorPort),
      Milliseconds(30)
    )
  }
}

