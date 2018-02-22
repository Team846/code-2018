package com.lynbrookrobotics.eighteen.lighting

import com.lynbrookrobotics.potassium.frc.{Color, LEDController, LEDControllerHardware}
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams._

object LightingTasks {

  class signalDriverForCubePickup(controller: LEDController)
                                 (implicit lightingHardware: LEDControllerHardware) extends ContinuousTask {

    override def onStart(): Unit = {
      controller.applySignal(Color(0, 255, 0))(lightingHardware)
    }

    override def onEnd(): Unit = {
      controller.resetToDefault()
    }
  }
}
