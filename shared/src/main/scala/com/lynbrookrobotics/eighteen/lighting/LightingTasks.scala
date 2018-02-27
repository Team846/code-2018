package com.lynbrookrobotics.eighteen.lighting

import com.lynbrookrobotics.potassium.frc.{Color, LEDController}
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

object LightingTasks {
  class signalDriverForCubePickup(controller: LEDController) extends ContinuousTask {
    override def onStart(): Unit = {
      controller.applySignal(Color(0, 255, 0))
    }

    override def onEnd(): Unit = {
      controller.resetToDefault()
    }
  }
}
