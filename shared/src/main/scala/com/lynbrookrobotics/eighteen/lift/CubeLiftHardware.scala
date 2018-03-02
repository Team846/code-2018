package com.lynbrookrobotics.eighteen.lift

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{FeedbackDevice, NeutralMode}
import com.lynbrookrobotics.eighteen.TalonManager
import com.lynbrookrobotics.potassium.commons.lift._
import com.lynbrookrobotics.potassium.frc.LazyTalon
import com.lynbrookrobotics.potassium.streams._
import squants.electro.ElectricPotential
import squants.space.Length
import squants.{Dimensionless, Each}

final case class CubeLiftHardware(talon: LazyTalon)(implicit coreTicks: Stream[Unit], props: CubeLiftProperties)
    extends LiftHardware {

  import props._

  TalonManager.configMaster(talon.t)

  talon.t.setInverted(false)
  talon.t.setSensorPhase(false)
  talon.t.setNeutralMode(NeutralMode.Brake)
  talon.t.configPeakOutputForward(props.maxMotorOutput.toEach, 0)
  talon.t.configPeakOutputReverse(-props.maxMotorOutput.toEach, 0)

  talon.t.configReverseSoftLimitThreshold(toNative(minHeight).toEach.toInt, 0)
  talon.t.configForwardSoftLimitThreshold(toNative(maxHeight).toEach.toInt, 0)
  talon.t.configForwardSoftLimitEnable(true, 0)
  talon.t.configReverseSoftLimitEnable(true, 0)

  talon.t.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, 0)

  val nativeReading: Stream[Dimensionless] =
    coreTicks.map(_ => Each(talon.t.getSelectedSensorPosition(0)))

  val position: Stream[Length] =
    nativeReading.map(props.fromNative)

  private val sensors = talon.t.getSensorCollection
  def readPotentiometerVoltage: ElectricPotential = props.talonOverVoltage.recip * Each(sensors.getAnalogInRaw)
  val potentiometerVoltage: Stream[ElectricPotential] =
    coreTicks.map(_ => readPotentiometerVoltage)
}

object CubeLiftHardware {
  def apply(config: CubeLiftConfig, coreTicks: Stream[Unit]): CubeLiftHardware = {
    CubeLiftHardware(
      new LazyTalon(new TalonSRX(config.ports.motorPort))
    )(coreTicks, config.props)
  }
}
