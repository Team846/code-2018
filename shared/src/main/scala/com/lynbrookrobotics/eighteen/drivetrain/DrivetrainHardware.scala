package com.lynbrookrobotics.eighteen.drivetrain

import com.ctre.phoenix.motorcontrol._
import com.ctre.phoenix.motorcontrol.can.{BaseMotorController, TalonSRX, VictorSPX}
import com.lynbrookrobotics.eighteen.TalonManager
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.frc.{LazyTalon, TalonEncoder}
import com.lynbrookrobotics.potassium.sensors.imu.{ADIS16448, DigitalGyro}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.{Ratio, Value3D}
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
  leftFollowerSRX: BaseMotorController,
  rightFollowerSRX: BaseMotorController,
  gyro: DigitalGyro,
  driverHardware: DriverHardware,
  props: DrivetrainProperties
) extends TwoSidedDriveHardware {
  override val track: Length = props.track

  val escIdx = 0
  val escTout = 0

  val left /*Back*/ =
    new LazyTalon(leftSRX)
  val right /*Back*/ =
    new LazyTalon(rightSRX)

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

  Set(leftSRX, rightSRX, leftFollowerSRX, rightFollowerSRX)
    .foreach(it => TalonManager.configSlave(it))

  Set(left, right).foreach { it =>
    TalonManager.configMaster(it.t)

    it.t.configContinuousCurrentLimit(maxCurrent.toAmperes.toInt, 0)
    it.t.configPeakCurrentLimit(maxCurrent.toAmperes.toInt, 0)
    it.t.configPeakCurrentDuration(0, 0)
    it.t.enableCurrentLimit(true)
  }

  private val t = Seconds(1)

  val rootDataStream: Stream[DrivetrainData] = Stream.periodic(Milliseconds(5))(
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
      if (config.ports.practiceSpeedControllers) {
        new TalonSRX(config.ports.rightFollowerPort)
      } else {
        new VictorSPX(config.ports.rightFollowerPort)
      },
      new ADIS16448(new SPI(SPI.Port.kMXP), null),
      driverHardware,
      config.props
    )
  }
}
