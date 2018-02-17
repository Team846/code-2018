package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.climber.ClimberWinch
import com.lynbrookrobotics.eighteen.climber.deployment.Deployment
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.forklift.Forklift
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}
import edu.wpi.first.networktables.NetworkTableInstance

import scala.collection.mutable
import com.lynbrookrobotics.potassium.{Component, Signal}

import scala.util.Try

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])(
  implicit val config: Signal[RobotConfig],
  hardware: RobotHardware,
  val clock: Clock
) {
  implicit val driverHardware: DriverHardware = hardware.driver

  implicit val drivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps = config.map(_.drivetrain.props)
  val drivetrain: Option[DrivetrainComponent] =
    Option(hardware.drivetrain).map(_ => new DrivetrainComponent(coreTicks))

  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.props)
  val collectorRollers: Option[CollectorRollers] =
    Option(hardware.collectorRollers).map(_ => new CollectorRollers(coreTicks))

  implicit val collectorClampHardware = hardware.collectorClamp
  val collectorClamp: Option[CollectorClamp] =
    Option(hardware.collectorClamp).map(_ => new CollectorClamp(coreTicks))

  implicit val collectorPivotHardware = hardware.collectorPivot
  val collectorPivot: Option[CollectorPivot] =
    Option(hardware.collectorPivot).map(_ => new CollectorPivot(coreTicks))

  implicit val climberDeploymentHardware = hardware.climberDeployment
  val climberDeployment: Option[Deployment] =
    Option(hardware.climberDeployment).map(_ => new Deployment(coreTicks))

  implicit val climberWinchHardware = hardware.climberWinch
  val climberWinch: Option[ClimberWinch] =
    Option(hardware.climberWinch).map(_ => new ClimberWinch(coreTicks))

  implicit val forkliftHardware = hardware.forklift
  val forklift: Option[Forklift] =
    Option(hardware.forklift).map(_ => new Forklift(coreTicks))

  implicit val cubeLiftHardware = hardware.cubeLift
  implicit val cubeLiftProps = config.map(_.cubeLift.props)
  val cubeLift: Option[CubeLiftComp] =
    Option(hardware.cubeLift).map(_ => new CubeLiftComp(coreTicks))

  lazy val components: Seq[Component[_]] = Seq(
    climberDeployment,
    climberWinch,
    collectorClamp,
    collectorPivot,
    collectorRollers,
    drivetrain,
    forklift,
    cubeLift
  ).flatten

  private val autonomousRoutines = mutable.Map.empty[Int, () => ContinuousTask]

  def addAutonomousRoutine(id: Int)(task: => ContinuousTask): Unit = {
    if (autonomousRoutines.contains(id)) {
      println(s"WARNING: overriding autonomous routine $id")
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

    addAutonomousRoutine(2) {
      generator.threeCubeAuto(drivetrain, collectorRollers, collectorClamp).toContinuous
    }
  }

  private val inst = NetworkTableInstance.getDefault()
  private val tab = inst.getTable("/SmartDashboard")
  private val ent = tab.getEntry("DB/Slider 0")

  driverHardware.isAutonomousEnabled.foreach(Signal {
    val autoID = Math.round(ent.getDouble(0)).toInt

    autonomousRoutines
      .getOrElse(autoID, {
        println(s"ERROR: autonomous routine $autoID not found")
        () =>
          FiniteTask.empty.toContinuous
      })
      .apply()
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

  val dashboard = Try {
    val dashboard = new FunkyDashboard(100, 8080)
    dashboard.start()
    dashboard
  }

  dashboard.failed.foreach(_.printStackTrace())

  dashboard.foreach { board =>
    import CoreRobot.ToTimeSeriesNumeric

    board
      .datasetGroup("Config")
      .addDataset(
        new JsonEditor("Robot Config")(
          configFileValue.get,
          updateConfigFile
        )
      )

    drivetrain.foreach { d =>
      board
        .datasetGroup("Drivetrain/Velocity")
        .addDataset(drivetrainHardware.leftVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Left Ground Velocity"))
      board
        .datasetGroup("Drivetrain/Velocity")
        .addDataset(
          drivetrainHardware.rightVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Right Ground Velocity")
        )
    }
  }
}

object CoreRobot {
  implicit class ToTimeSeriesNumeric[T](val stream: Stream[T]) extends AnyVal {
    def toTimeSeriesNumeric(name: String)(implicit ev: T => Double): TimeSeriesNumeric = {
      var lastValue: Double = 0.0
      new TimeSeriesNumeric(name)(lastValue) {
        val cancel = stream.foreach { v =>
          lastValue = v
        }
      }
    }
  }
}
