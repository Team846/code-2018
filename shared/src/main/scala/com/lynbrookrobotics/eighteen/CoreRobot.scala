package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComp
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}
import edu.wpi.first.networktables.NetworkTableInstance
import squants.Each

import scala.collection.mutable

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])
               (implicit val config: Signal[RobotConfig], hardware: RobotHardware,
                 val clock: Clock) {
  implicit val driverHardware: DriverHardware = hardware.driver
  private val ds = driverHardware.station

  implicit val drivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps = config.map(_.drivetrain.props)
  val drivetrain: Option[DrivetrainComp] =
    Option(hardware.drivetrain).map(_ => new DrivetrainComp(coreTicks))

  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.props)
  val collectorRollers: Option[CollectorRollers] =
    Option(hardware.collectorRollers).map(_ => new CollectorRollers(coreTicks))

  implicit val collectorClampHardware = hardware.collectorClamp
  implicit val collectorClampProps = config.map(_.collectorClamp)
  val collectorClamp: Option[CollectorClamp] =
    Option(hardware.collectorClamp).map(_ => new CollectorClamp(coreTicks))

  lazy val components: Seq[Component[_]] = Seq(
    drivetrain,
    collectorRollers,
    collectorClamp
  ).flatten

  private var autonomousRoutines = mutable.Map.empty[Int, () => ContinuousTask]

  println("add auto")

  def addAutonomousRoutine(id: Int)(task:  => ContinuousTask): Unit = {
    if (autonomousRoutines.contains(id)) {
      println(s"WARNING, overriding autonomous routine $id")
    }

    autonomousRoutines(id) = () => task
  }

  val generator = new AutoGenerator(this)

  for {
    drivetrain <- drivetrain
    collectorRollers <- collectorRollers
    collectorClamp <- collectorClamp
  } {
    addAutonomousRoutine(1) {
      generator.twoCubeAuto(drivetrain, collectorRollers, collectorClamp).toContinuous
    }
  }

  val inst = NetworkTableInstance.getDefault()
  val tab = inst.getTable("/SmartDashboard")
  val ent = tab.getEntry("DB/Slider 0")

  driverHardware.isAutonomousEnabled.foreach(Signal {
    val autoID = Math.round(ent.getDouble(0)).toInt
    println(s"autoid: $autoID")

    autonomousRoutines.getOrElse(autoID, {
      println(s"ERROR: autonomous routine $autoID not found")
      () => FiniteTask.empty.toContinuous
    }).apply()
  })

  // Register at the end so they are all run first
  driverHardware.isTeleopEnabled.onStart.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  driverHardware.isEnabled.onStart.foreach { () =>
    if (drivetrain.isDefined) {
      drivetrainHardware.gyro.endCalibration()
    }
  }

  driverHardware.isEnabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  ButtonMappings.setup(this)
}
