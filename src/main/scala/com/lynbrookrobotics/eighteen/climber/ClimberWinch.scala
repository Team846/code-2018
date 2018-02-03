package com.lynbrookrobotics.eighteen.climber


import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.{Component, streams}
import squants.{Dimensionless, Each, Percent}

class ClimberWinch(val coreTicks: streams.Stream[Unit])(implicit hardware: ClimberWinchHardware) extends Component[Dimensionless]{
  override def defaultController: streams.Stream[Dimensionless] = coreTicks.mapToConstant(Each(0))

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.leftMotor.set(ControlMode.PercentOutput, signal.toEach)
    hardware.middleMotor.set(ControlMode.PercentOutput, signal.toEach)
    hardware.rightMotor.set(ControlMode.PercentOutput, signal.toEach)
  }

}
