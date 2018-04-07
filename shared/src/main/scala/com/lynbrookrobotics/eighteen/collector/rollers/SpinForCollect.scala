package com.lynbrookrobotics.eighteen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Percent
import squants.time.Milliseconds

class SpinForCollect(rollers: CollectorRollers)(implicit props: Signal[CollectorRollersProperties])
    extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(
      rollers.coreTicks
        .map(_ => (-props.get.collectSpeed, -props.get.collectSpeed))
        .map((_, Milliseconds(System.currentTimeMillis())))
        .map {
          case ((l, r), t) =>
            val freq = props.get.sqrWaveFreq.toHertz
            val sqrAmpl = props.get.sqrWaveAmpl
            (
              l + ((t.toSeconds.toInt * freq) % 2 * sqrAmpl),
              r + ((t.toSeconds.toInt * freq + 1) % 2 * sqrAmpl)
            )
        }
    )
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
