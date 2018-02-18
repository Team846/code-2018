package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Dimensionless

class RollersManualControl(target: Stream[Dimensionless])(rollers: CollectorRollers)(
  implicit props: Signal[CollectorRollersProperties]
) extends ContinuousTask() {
  override protected def onStart(): Unit = rollers.setController(
    target.map(it => (it, it))
  )

  override protected def onEnd(): Unit = rollers.resetToDefault()
}
