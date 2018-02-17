package com.lynbrookrobotics.eighteen.collector

import com.lynbrookrobotics.eighteen.collector.clamp.{CollectorClamp, OpenCollector}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, CollectorRollersProperties, SpinForCollect, SpinForPurge}
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

object CollectorTasks {
  def collectCube(rollers: CollectorRollers, clamp: CollectorClamp)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForCollect(rollers) and new OpenCollector(clamp)
  }

  def collectCubeWithoutOpen(
    rollers: CollectorRollers
  )(implicit collectorRollersProps: Signal[CollectorRollersProperties]): ContinuousTask = {
    new SpinForCollect(rollers)
  }

  def purgeCube(
    rollers: CollectorRollers
  )(implicit collectorRollersProps: Signal[CollectorRollersProperties]): ContinuousTask = {
    new SpinForPurge(rollers)
  }

  def purgeCubeOpen(rollers: CollectorRollers, clamp: CollectorClamp)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForPurge(rollers) and new OpenCollector(clamp)
  }
}
