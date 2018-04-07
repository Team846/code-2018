package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Percent
import squants.time.Milliseconds

class SpinForCollect(rollers: CollectorRollers)(implicit collectorRollersProps: Signal[CollectorRollersProperties])
  extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(
      rollers.coreTicks
        .map(_ => (-collectorRollersProps.get.collectSpeed, -collectorRollersProps.get.collectSpeed))
        .map((_, Milliseconds(System.currentTimeMillis())))
        .map { case ((l, r), t) => (
            l + ((t.toSeconds.toInt * 4) % 2 * Percent(20)),
            r + ((t.toSeconds.toInt * 4 + 1) % 2 * Percent(20))
        )}
    )
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
