package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.climber.deployment.Deployment
import com.lynbrookrobotics.eighteen.climber.winch.ClimberWinch
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.driver.DriverHardware
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.forklift.Forklift
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.eighteen.lighting.LightingTasks
import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ContinuousEvent
import com.lynbrookrobotics.potassium.frc.LEDController
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}
import edu.wpi.first.networktables.NetworkTableInstance

import scala.collection.mutable
import com.lynbrookrobotics.potassium.{Component, Signal}
import edu.wpi.first.wpilibj.DriverStation

import scala.util.Try

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])(
  implicit val config: Signal[RobotConfig],
  hardware: RobotHardware,
  val clock: Clock
) {
  implicit val driverHardware: DriverHardware = hardware.driver

  implicit val drivetrainHardware = hardware.drivetrain.orNull
  implicit val drivetrainProps = config.map(_.drivetrain.get.props)
  val drivetrain: Option[DrivetrainComponent] =
    hardware.drivetrain.map(_ => new DrivetrainComponent(coreTicks))

  implicit val collectorRollersHardware = hardware.collectorRollers.orNull
  implicit val collectorRollersProps = config.map(_.collectorRollers.get.props)
  val collectorRollers: Option[CollectorRollers] =
    hardware.collectorRollers.map(_ => new CollectorRollers(coreTicks))

  implicit val collectorClampHardware = hardware.collectorClamp.orNull
  implicit val collectorClampProps = config.map(_.collectorClamp.get.props)

  val collectorClamp: Option[CollectorClamp] =
    hardware.collectorClamp.map(_ => new CollectorClamp(coreTicks))

  implicit val collectorPivotHardware = hardware.collectorPivot.orNull
  val collectorPivot: Option[CollectorPivot] =
    hardware.collectorPivot.map(_ => new CollectorPivot(coreTicks))

  implicit val climberDeploymentHardware = hardware.climberDeployment.orNull
  val climberDeployment: Option[Deployment] =
    hardware.climberDeployment.map(_ => new Deployment(coreTicks))

  implicit val climberWinchHardware = hardware.climberWinch.orNull
  implicit val climberWinchProps = config.map(_.climberWinch.get.props)
  val climberWinch: Option[ClimberWinch] =
    hardware.climberWinch.map(_ => new ClimberWinch(coreTicks))

  implicit val forkliftHardware = hardware.forklift.orNull
  val forklift: Option[Forklift] =
    hardware.forklift.map(_ => new Forklift(coreTicks))

  implicit val cubeLiftHardware = hardware.cubeLift.orNull
  implicit val cubeLiftProps = config.map(_.cubeLift.get.props)
  val cubeLiftComp: Option[CubeLiftComp] =
    hardware.cubeLift.map(_ => new CubeLiftComp(coreTicks))

  val cameraHardware = hardware.camera

  implicit val lightingHardware = hardware.ledHardware.orNull
  implicit val lightingComponent: Option[LEDController] =
    hardware.ledHardware.map(_ => new LEDController(
      coreTicks,
      Signal.constant(DriverStation.Alliance))
    )

  lazy val components: Seq[Component[_]] = Seq(
    climberDeployment,
    climberWinch,
    collectorClamp,
    collectorPivot,
    collectorRollers,
    drivetrain,
    forklift,
    cubeLiftComp,
    lightingComponent
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
    collectorPivot <- collectorPivot
  } {
    addAutonomousRoutine(1) {
      generator.twoCubeAuto(drivetrain, collectorRollers, collectorClamp, collectorPivot).toContinuous
    }

    addAutonomousRoutine(2) {
      generator.threeCubeAuto(drivetrain, collectorRollers, collectorClamp, collectorPivot).toContinuous
    }
  }

  for {
    camera <- cameraHardware
    lighting <- lightingComponent
  } {
    val hasTargetEvent: ContinuousEvent = camera.hasTarget.eventWhen(identity)
    hasTargetEvent.foreach(new LightingTasks.signalDriverForCubePickup(lighting))
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
    drivetrain.foreach { _ =>
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

    cubeLiftComp.foreach { l =>
      board
        .datasetGroup("CubeLift/Position")
        .addDataset(cubeLiftHardware.potentiometerVoltage.map(_.toVolts).toTimeSeriesNumeric("Potentiometer Voltage"))

      board
        .datasetGroup("CubeLift/Position")
        .addDataset(cubeLiftHardware.position.map(_.toFeet).toTimeSeriesNumeric("Lift Position"))

      board
        .datasetGroup("CubeLift/Position")
        .addDataset(cubeLiftHardware.nativeReading.map(_.toEach).toTimeSeriesNumeric("Talon Native Position"))

      board
        .datasetGroup("CubeLift/Current")
        .addDataset(
          coreTicks.map(_ => cubeLiftHardware.talon.t.getOutputCurrent).toTimeSeriesNumeric("TalonSRX Current Draw")
        )
    }

    collectorClamp.foreach { c =>
      board
        .datasetGroup("Collector/Proximity")
        .addDataset(
          collectorClampHardware.proximitySensorReading.map(_.toVolts).toTimeSeriesNumeric("Proximity Sensor Voltage")
        )
    }
  }
}

object CoreRobot {

  implicit class ToTimeSeriesNumeric[T](val stream: Stream[T]) extends AnyVal {
    def toTimeSeriesNumeric(name: String)(implicit ev: T => Double): TimeSeriesNumeric = {
      var lastValue: Double = 0.0
      new TimeSeriesNumeric(name)(lastValue) {
        stream.foreach { v =>
          lastValue = v
        }
      }
    }
  }

}
