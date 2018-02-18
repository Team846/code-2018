package com.lynbrookrobotics.eighteen.collector

import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenCollector}
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, CollectorRollersProperties, SpinForCollect, SpinForPurge}
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

object CollectorTasks {
  def collectCube(rollers: CollectorRollers, clamp: CollectorClamp, pivot: CollectorPivot)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForCollect(rollers) and new OpenCollector(clamp) and new PivotDown(pivot)
  }

  def collectCubeWithoutOpen(rollers: CollectorRollers, pivot: CollectorPivot)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForCollect(rollers) and new PivotDown(pivot)
  }

  def purgeCube(rollers: CollectorRollers, pivot: CollectorPivot)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForPurge(rollers) and new PivotDown(pivot)
  }

  def purgeCubeOpen(rollers: CollectorRollers, clamp: CollectorClamp, pivot: CollectorPivot)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForPurge(rollers) and new OpenCollector(clamp) and new PivotDown(pivot)
  }
}
