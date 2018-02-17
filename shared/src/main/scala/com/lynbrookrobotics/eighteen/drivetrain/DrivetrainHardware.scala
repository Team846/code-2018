package com.lynbrookrobotics.eighteen.drivetrain

import com.ctre.phoenix.motorcontrol.can.{TalonSRX, VictorSPX}
import com.ctre.phoenix.motorcontrol._
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{LazyTalon, TalonEncoder}
import com.lynbrookrobotics.potassium.sensors.imu.{ADIS16448, DigitalGyro}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.{Ratio, Value3D}
import com.lynbrookrobotics.potassium.frc.Implicits._
import edu.wpi.first.wpilibj.SPI
import squants.motion.AngularVelocity
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Length, Velocity}

final case class DrivetrainData(
  leftEncoderVelocity: AngularVelocity,
  rightEncoderVelocity: AngularVelocity,
  leftEncoderRotation: Angle,
  rightEncoderRotation: Angle,
  gyroVelocities: Value3D[AngularVelocity]
)

final case class DrivetrainHardware(
  coreTicks: Stream[Unit],
  leftSRX: TalonSRX,
  rightSRX: TalonSRX,
  leftFollowerSRX: VictorSPX,
  rightFollowerSRX: VictorSPX,
  gyro: DigitalGyro,
  driverHardware: DriverHardware,
  props: DrivetrainProperties
) extends TwoSidedDriveHardware {
  override val track: Length = props.track

  val escIdx = 0
  val escTout = 0

  val left /*Back*/ =
    new LazyTalon(leftSRX, escIdx, escTout, defaultPeakOutputReverse = -1.0, defaultPeakOutputForward = 1.0)
  val right /*Back*/ =
    new LazyTalon(rightSRX, escIdx, escTout, defaultPeakOutputReverse = -1.0, defaultPeakOutputForward = 1.0)

  import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced._

  Set(left, right).map(_.t).foreach { it =>
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

    it.configContinuousCurrentLimit(30, escTout)
    it.configPeakCurrentDuration(0, escTout)
    it.enableCurrentLimit(true)

    Map(
      Status_1_General -> 10,
      Status_2_Feedback0 -> 20,
      Status_12_Feedback1 -> 20,
      Status_3_Quadrature -> 100,
      Status_4_AinTempVbat -> 100
    ).foreach {
      case (frame, period) =>
        it.setStatusFramePeriod(frame, period, escTout)
    }
  }

  Set(leftFollowerSRX, rightFollowerSRX).foreach { it =>
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

  leftFollowerSRX.follow(left.t)
  rightFollowerSRX.follow(right.t)

  right.t.setInverted(true)
  rightFollowerSRX.setInverted(true)
  right.t.setSensorPhase(false)

  import props._

  val leftEncoder = new TalonEncoder(left.t, encoderAngleOverTicks)
  val rightEncoder = new TalonEncoder(right.t, encoderAngleOverTicks)

  left.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)
  right.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)

  StatusFrame.values().foreach { it =>
    right.t.setStatusFramePeriod(it, 1000, escTout)
    left.t.setStatusFramePeriod(it, 1000, escTout)
  }

  Set(left, right).foreach { it =>
    it.t.setStatusFramePeriod(Status_1_General, 5, escTout)
    it.t.setStatusFramePeriod(Status_2_Feedback0, 10, escTout)

    it.t.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTout)
    it.t.configVelocityMeasurementWindow(4, escTout)
  }

  private val t = Seconds(1)

  val rootDataStream = Stream.periodic(Milliseconds(5))(
    DrivetrainData(
      leftEncoder.getAngularVelocity,
      rightEncoder.getAngularVelocity,
      leftEncoder.getAngle,
      rightEncoder.getAngle,
      gyro.getVelocities
    )
  )

  override val leftVelocity: Stream[Velocity] = rootDataStream.map(_.leftEncoderVelocity).map { av =>
    val x = wheelOverEncoderGears * Ratio(av * t, t)
    (x.num / x.den) onRadius (wheelDiameter / 2)
  }

  override val rightVelocity: Stream[Velocity] = rootDataStream.map(_.rightEncoderVelocity).map { av =>
    val x = wheelOverEncoderGears * Ratio(av * t, t)
    (x.num / x.den) onRadius (wheelDiameter / 2)
  }

  override val leftPosition: Stream[Length] = rootDataStream.map(_.leftEncoderRotation).map { ar =>
    (wheelOverEncoderGears * ar) onRadius (wheelDiameter / 2)
  }

  override val rightPosition: Stream[Length] = rootDataStream.map(_.rightEncoderRotation).map { ar =>
    (wheelOverEncoderGears * ar) onRadius (wheelDiameter / 2)
  }

  override lazy val turnVelocity: Stream[AngularVelocity] = rootDataStream.map(_.gyroVelocities).map(_.z)
  override lazy val turnPosition: Stream[Angle] = turnVelocity.integral.preserve
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig, coreTicks: Stream[Unit], driverHardware: DriverHardware): DrivetrainHardware = {
    new DrivetrainHardware(
      coreTicks,
      new TalonSRX(config.ports.leftPort),
      new TalonSRX(config.ports.rightPort),
      new VictorSPX(config.ports.leftFollowerPort),
      new VictorSPX(config.ports.rightFollowerPort),
      new ADIS16448(new SPI(SPI.Port.kMXP), null),
      driverHardware,
      config.props
    )
  }
}
