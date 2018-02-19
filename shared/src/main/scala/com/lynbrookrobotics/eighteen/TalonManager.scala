package com.lynbrookrobotics.eighteen

import com.ctre.phoenix.motorcontrol.can.{BaseMotorController, TalonSRX}
import com.ctre.phoenix.motorcontrol.{NeutralMode, StatusFrame, StatusFrameEnhanced, VelocityMeasPeriod}
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced

import scala.collection.Map

object TalonManager {
  private val escTout = 0

  def resetTalonToDefaults(it: BaseMotorController): Unit = {
    it.setNeutralMode(NeutralMode.Coast)
    it.configOpenloopRamp(0, escTout)
    it.configClosedloopRamp(0, escTout)

    it.configPeakOutputReverse(-1, escTout)
    it.configNominalOutputReverse(0, escTout)
    it.configNominalOutputForward(0, escTout)
    it.configPeakOutputForward(1, escTout)
    it.configNeutralDeadband(0.001 /*min*/, escTout)

    it.configVoltageCompSaturation(11, escTout)
    it.configVoltageMeasurementFilter(32, escTout)
    it.enableVoltageCompensation(true)
  }

  def configMaster(it: TalonSRX): Unit = {
    resetTalonToDefaults(it)
    import StatusFrameEnhanced._

    Map( // DEFAULTS
      Status_1_General -> 10,
      Status_2_Feedback0 -> 20,
      Status_12_Feedback1 -> 20,
      Status_3_Quadrature -> 100,
      Status_4_AinTempVbat -> 100
    ).foreach { case (frame, period) =>
      it.setStatusFramePeriod(frame, period, escTout)
    }

    it.setStatusFramePeriod(Status_1_General, 5, escTout)
    it.setStatusFramePeriod(Status_2_Feedback0, 10, escTout)

    it.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTout)
    it.configVelocityMeasurementWindow(4, escTout)
  }

  def configSlave(t: BaseMotorController): Unit = {
    StatusFrame.values().foreach {
      it => t.setStatusFramePeriod(it, 1000, escTout)
    }
  }
}
