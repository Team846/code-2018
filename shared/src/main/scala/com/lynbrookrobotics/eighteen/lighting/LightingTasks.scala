package com.lynbrookrobotics.eighteen.lighting

import com.lynbrookrobotics.potassium.frc.{Color, LEDController, LEDControllerHardware}
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

object LightingTasks {

  class signalDriverForCubePickup(hardware: LEDControllerHardware,
                                  controller: LEDController,
                                  hasTarget: Stream[Boolean]) extends ContinuousTask {

    override def onStart(): Unit = {
      hasTarget.foreach(
        if (_) {
          controller.applySignal(Color(0, 255, 0))(hardware)
        } else {
          controller.resetToDefault()
        }
      )
    }

    override def onEnd(): Unit = {
      controller.resetToDefault()
    }
  }
}
