package com.lynbrookrobotics.eighteen.collector

import com.lynbrookrobotics.eighteen.collector.clamp.{ClampCollector, CollectorClamp}
import com.lynbrookrobotics.eighteen.collector.rollers.{CollectorRollers, SpinForCollect, SpinForPurge}
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

object CollectorTasks {
  def collectCubeClamped(rollers: CollectorRollers, clamp: CollectorClamp): ContinuousTask = {
    new SpinForCollect(rollers) and new ClampCollector(clamp)
  }

  def purgeCubeClamped(rollers: CollectorRollers, clamp: CollectorClamp): ContinuousTask = {
    new SpinForPurge(rollers) and new ClampCollector(clamp)
  }
}
