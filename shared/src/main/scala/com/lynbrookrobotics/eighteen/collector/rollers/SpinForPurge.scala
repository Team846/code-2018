package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class SpinForPurge(rollers: CollectorRollers)(implicit collectorRollersProps: Signal[CollectorRollersProperties])
    extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(
      rollers.coreTicks.map(_ => (collectorRollersProps.get.purgeSpeed, collectorRollersProps.get.purgeSpeed))
    )
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}

class SpinForHardPurge(rollers: CollectorRollers)(implicit collectorRollersProps: Signal[CollectorRollersProperties])
    extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(
      rollers.coreTicks.map(_ => (collectorRollersProps.get.purgeSpeed * 2, collectorRollersProps.get.purgeSpeed * 2))
    )
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
