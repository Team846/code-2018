package com.lynbrookrobotics.eighteen.climber.deployment

import com.lynbrookrobotics.eighteen.SingleOutputChecker
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream

trait DeploymentState

case object DeploymentOn extends DeploymentState
case object DeploymentOff extends DeploymentState

class Deployment(val coreTicks: Stream[Unit])(implicit hardware: DeploymentHardware)
    extends Component[DeploymentState] {
  override def defaultController: Stream[DeploymentState] = coreTicks.mapToConstant(DeploymentOff)

  private val check = new SingleOutputChecker(
    "Climber Deployment Solenoid",
    hardware.deploymentSolenoid.get
  )

  override def applySignal(signal: DeploymentState): Unit = check.assertSingleOutput { () =>
    signal match {
      case DeploymentOn =>
        hardware.deploymentSolenoid.set(true)
      case DeploymentOff =>
        hardware.deploymentSolenoid.set(false)
    }
  }
}
