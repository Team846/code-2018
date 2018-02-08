package com.lynbrookrobotics.eighteen.collector

import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenClamp}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, CollectorRollersProperties}
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class CollectCubeClamped(clamp: CollectorClamp, rollers: CollectorRollers)
                        (implicit collectorRollersProps: Signal[CollectorRollersProperties]) extends ContinuousTask {
  override protected def onStart(): Unit = {
    clamp.setController(clamp.coreTicks.mapToConstant(OpenClamp))
    rollers.setController(rollers.coreTicks.map(_ =>
      (collectorRollersProps.get.collectSpeed, -collectorRollersProps.get.collectSpeed)
    ))
  }

  override protected def onEnd(): Unit = {
    clamp.resetToDefault()
    rollers.resetToDefault()
  }
}
