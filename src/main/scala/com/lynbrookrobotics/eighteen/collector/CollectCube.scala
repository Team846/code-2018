package com.lynbrookrobotics.eighteen.collector

import com.lynbrookrobotics.eighteen.collector.clamp.{ClosedClamp, CollectorClamp}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, CollectorRollersConfig}
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream

class CollectCube(clamp: CollectorClamp, rollers: CollectorRollers)(implicit collectorRollersConfig: Stream[CollectorRollersConfig]) extends ContinuousTask {
  override protected def onStart(): Unit = {
    clamp.setController(clamp.coreTicks.mapToConstant(ClosedClamp))
    rollers.setController(collectorRollersConfig.map(x => (x.collectSpeed, x.collectSpeed)))
  }

  override protected def onEnd(): Unit = {
    clamp.resetToDefault()
    rollers.resetToDefault()
  }
}
