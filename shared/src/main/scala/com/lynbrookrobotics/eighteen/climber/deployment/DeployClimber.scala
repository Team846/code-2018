package com.lynbrookrobotics.eighteen.climber.deployment

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class DeployClimber(deployment: Deployment) extends ContinuousTask{
  override protected def onStart(): Unit = deployment.setController(deployment.coreTicks.mapToConstant(DeploymentOn))

  override protected def onEnd(): Unit = deployment.resetToDefault()
}
