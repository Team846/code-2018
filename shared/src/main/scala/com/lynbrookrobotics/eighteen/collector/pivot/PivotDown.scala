package com.lynbrookrobotics.eighteen.collector.pivot

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class PivotDown(pivot: CollectorPivot) extends ContinuousTask {
  override protected def onStart(): Unit =
    pivot.setController(pivot.coreTicks.map(_ => PivotDownState))

  override protected def onEnd(): Unit =
    pivot.resetToDefault()
}
