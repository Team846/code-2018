package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class SpinForPurge(rollers: CollectorRollers)(implicit collectorRollersProps: Signal[CollectorRollersProperties])
    extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(
      rollers.coreTicks.map(_ => (-collectorRollersProps.get.collectSpeed, collectorRollersProps.get.collectSpeed))
    )
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
