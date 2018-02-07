package com.lynbrookrobotics.eighteen.drivetrain

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol._
import StatusFrameEnhanced._
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{LazyTalon, TalonEncoder}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.time.Seconds
import squants.{Length, Velocity}

class DrivetrainHardware(coreTicks: Stream[Unit],
                         leftSRX: TalonSRX,
                         rightSRX: TalonSRX,
                         leftFollowerSRX: TalonSRX,
                         rightFollowerSRX: TalonSRX,
                         val driverHardware: DriverHardware,
                         props: DrivetrainProperties)(implicit clock: Clock) extends TwoSidedDriveHardware {
  override val track: Length = props.track

  val escIdx = 0
  val escTout = 0

  val left /*Back*/ = new LazyTalon(leftSRX, escIdx, escTout,
    defaultPeakOutputReverse = -1.0,
    defaultPeakOutputForward = 1.0
  )
  val right /*Back*/ = new LazyTalon(rightSRX, escIdx, escTout,
    defaultPeakOutputReverse = -1.0,
    defaultPeakOutputForward = 1.0
  )

  val leftFollower /*Front*/ = new LazyTalon(leftFollowerSRX, escIdx, escTout,
    defaultPeakOutputReverse = -1.0,
    defaultPeakOutputForward = 1.0
  )

  val rightFollower /*Front*/ = new LazyTalon(rightFollowerSRX, escIdx, escTout,
    defaultPeakOutputReverse = -1.0,
    defaultPeakOutputForward = 1.0
  )

  Set(left, right, leftFollower, rightFollower)
    .map(_.t)
    .foreach { it =>
      it.setNeutralMode(NeutralMode.Coast)
      it.configOpenloopRamp(0, escTout)
      it.configClosedloopRamp(0, escTout)

      it.configPeakOutputReverse(-1, escTout)
      it.configNominalOutputReverse(0, escTout)
      it.configNominalOutputForward(0, escTout)
      it.configPeakOutputForward(1, escTout)
      it.configNeutralDeadband(0.001 /*min*/ , escTout)

      it.configVoltageCompSaturation(11, escTout)
      it.configVoltageMeasurementFilter(32, escTout)
      it.enableVoltageCompensation(true)

      it.configContinuousCurrentLimit(75, escTout)
      it.configPeakCurrentDuration(0, escTout)
      it.enableCurrentLimit(true)
      Map(
        Status_1_General -> 10,
        Status_2_Feedback0 -> 20,
        Status_12_Feedback1 -> 20,
        Status_3_Quadrature -> 100,
        Status_4_AinTempVbat -> 100
      ).foreach { case (frame, period) =>
        it.setStatusFramePeriod(frame, period, escTout)
      }
    }

  leftFollower.t.follow(left.t)
  rightFollower.t.follow(right.t)

  right.t.setInverted(true)
  rightFollower.t.setInverted(true)
  right.t.setSensorPhase(false)

  import props._

  val leftEncoder = new TalonEncoder(left.t, encoderAngleOverTicks)
  val rightEncoder = new TalonEncoder(right.t, encoderAngleOverTicks)

  left.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)
  right.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)

  import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced._
  StatusFrame.values().foreach { it =>
    right.t.setStatusFramePeriod(it, 1000, escTout)
    left.t.setStatusFramePeriod(it, 1000, escTout)
    rightFollower.t.setStatusFramePeriod(it, 1000, escTout)
    leftFollower.t.setStatusFramePeriod(it, 1000, escTout)
  }

  Set(left, right).foreach { it =>
    it.t.setStatusFramePeriod(Status_1_General, 5, escTout)
    it.t.setStatusFramePeriod(Status_2_Feedback0, 10, escTout)

    it.t.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTout)
    it.t.configVelocityMeasurementWindow(4, escTout)
  }

  private val t = Seconds(1)
  override val leftVelocity: Stream[Velocity] = coreTicks.map { _ =>
    val x = wheelOverEncoderGears * Ratio(leftEncoder.getAngularVelocity * t, t)
    (x.num / x.den) onRadius (wheelDiameter / 2)
  }

  override val rightVelocity: Stream[Velocity] = coreTicks.map { _ =>
    val x = wheelOverEncoderGears * Ratio(rightEncoder.getAngularVelocity * t, t)
    (x.num / x.den) onRadius (wheelDiameter / 2)
  }

  override val leftPosition: Stream[Length] = coreTicks.map { _ =>
    (wheelOverEncoderGears * leftEncoder.getAngle) onRadius (wheelDiameter / 2)
  }

  override val rightPosition: Stream[Length] = coreTicks.map { _ =>
    (wheelOverEncoderGears * rightEncoder.getAngle) onRadius (wheelDiameter / 2)
  }
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig, coreTicks: Stream[Unit],
            driverHardware: DriverHardware)(implicit clock: Clock): DrivetrainHardware = {
    new DrivetrainHardware(
      coreTicks,
      new TalonSRX(config.ports.leftPort),
      new TalonSRX(config.ports.rightPort),
      new TalonSRX(config.ports.leftFollowerPort),
      new TalonSRX(config.ports.rightFollowerPort),
      driverHardware,
      config.props
    )
  }
}