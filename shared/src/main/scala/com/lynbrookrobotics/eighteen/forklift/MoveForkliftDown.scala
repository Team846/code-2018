package com.lynbrookrobotics.eighteen.forklift

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class MoveForkliftDown(forklift: Forklift) extends ContinuousTask {
  override protected def onStart(): Unit = forklift.setController(
    forklift.coreTicks.map(_ => ForkliftDown)
  )

  override protected def onEnd(): Unit = forklift.resetToDefault()
}
