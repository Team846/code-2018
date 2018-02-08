package com.lynbrookrobotics.eighteen.drivetrain


import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{FeedbackDevice, StatusFrame, VelocityMeasPeriod}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.frc.{LazyTalon, TalonEncoder}
import com.lynbrookrobotics.potassium.sensors.imu.{ADIS16448, DigitalGyro}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import edu.wpi.first.wpilibj.SPI
import squants.motion.AngularVelocity
import squants.space.Degrees
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Each, Length, Time, Velocity}

case class DrivetrainData(leftEncoderVelocity: AngularVelocity,
                          rightEncoderVelocity: AngularVelocity,
                          leftEncoderRotation: Angle,
                          rightEncoderRotation: Angle,
                          gyroVelocities: Value3D[AngularVelocity])

case class DrivetrainHardware(leftMaster: LazyTalon, leftSlave: LazyTalon,
                              rightMaster: LazyTalon, rightSlave: LazyTalon,
                              gyro: DigitalGyro,
                              props: DrivetrainProperties,
                              //driverHardware: DriverHardware,
                              period: Time,
                              idx: Int)(implicit clock: Clock)
  extends TwoSidedDriveHardware {
  rightMaster.t.setInverted(true)
  rightSlave.t.setInverted(true)

  rightSlave.t.follow(rightMaster.t)
  leftSlave.t.follow(leftMaster.t)
  leftMaster.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, idx, 0)
  rightMaster.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, idx, 0)

  import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced._

  StatusFrame.values().foreach { it =>
    rightMaster.t.setStatusFramePeriod(it, 1000, 0)
    leftMaster.t.setStatusFramePeriod(it, 1000, 0)
    rightSlave.t.setStatusFramePeriod(it, 1000, 0)
    leftSlave.t.setStatusFramePeriod(it, 1000, 0)
  }

  Set(leftMaster, rightMaster).foreach { it =>
    it.t.setStatusFramePeriod(Status_1_General, 5, 0)
    it.t.setStatusFramePeriod(Status_2_Feedback0, 10, 0)

    it.t.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, 0)
    it.t.configVelocityMeasurementWindow(4, 0)
  }

  val leftEncoder = new TalonEncoder(leftMaster.t, Degrees(360) / Each(8192))
  val rightEncoder = new TalonEncoder(rightMaster.t, Degrees(360) / Each(8192))

  val wheelRadius: Length = props.wheelDiameter / 2
  val track: Length = props.track

  val rootDataStream: Stream[DrivetrainData] = Stream.periodic(period) {
    DrivetrainData(
      leftEncoder.getAngularVelocity,
      rightEncoder.getAngularVelocity,

      leftEncoder.getAngle,
      rightEncoder.getAngle,

      gyro.getVelocities
    )
  }

  override val leftVelocity: Stream[Velocity] = rootDataStream.map(_.leftEncoderVelocity).map(av =>
    wheelRadius * (av.toRadiansPerSecond * props.gearRatio) / Seconds(1))

  override val rightVelocity: Stream[Velocity] = rootDataStream.map(_.rightEncoderVelocity).map(av =>
    wheelRadius * (av.toRadiansPerSecond * props.gearRatio) / Seconds(1))

  val leftPosition: Stream[Length] = rootDataStream.map(_.leftEncoderRotation).map(a =>
    a.toRadians * props.gearRatio * wheelRadius)
  val rightPosition: Stream[Length] = rootDataStream.map(_.rightEncoderRotation).map(a =>
    a.toRadians * props.gearRatio * wheelRadius)

  override lazy val turnVelocity: Stream[AngularVelocity] = rootDataStream.map(_.gyroVelocities).map(_.z)
  override lazy val turnPosition: Stream[Angle] = turnVelocity.integral.preserve
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig)(implicit clock: Clock): DrivetrainHardware = {
    DrivetrainHardware(
      new LazyTalon(new TalonSRX(config.ports.leftBack), config.idx, 0,
        defaultPeakOutputReverse = -1, defaultPeakOutputForward = 1
      ),
      new LazyTalon(new TalonSRX(config.ports.leftFront), config.idx, 0,
        defaultPeakOutputReverse = -1, defaultPeakOutputForward = 1
      ),
      new LazyTalon(new TalonSRX(config.ports.rightBack), config.idx, 0,
        defaultPeakOutputReverse = -1, defaultPeakOutputForward = 1
      ),
      new LazyTalon(new TalonSRX(config.ports.rightFront), config.idx, 0,
        defaultPeakOutputReverse = -1, defaultPeakOutputForward = 1
      ),
      new ADIS16448(new SPI(SPI.Port.kMXP), null),
      config.properties,
      Milliseconds(5),
      idx = config.idx
    )
  }
}