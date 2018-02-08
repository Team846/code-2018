package com.lynbrookrobotics.eighteen.collector.clamp

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ClampCollector(clamp: CollectorClamp) extends ContinuousTask {
  override protected def onStart(): Unit = {
    clamp.setController(clamp.coreTicks.mapToConstant(OpenClamp))
  }

  override protected def onEnd(): Unit = {
    clamp.resetToDefault()
  }
}
