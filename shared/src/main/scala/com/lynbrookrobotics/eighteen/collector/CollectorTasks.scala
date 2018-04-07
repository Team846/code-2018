package com.lynbrookrobotics.eighteen.collector

import com.lynbrookrobotics.eighteen.collector.clamp._
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers._
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}

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

  def purgeCubeHard(rollers: CollectorRollers, pivot: CollectorPivot)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForHardPurge(rollers) and new PivotDown(pivot)
  }

  def purgeCubeOpen(rollers: CollectorRollers, clamp: CollectorClamp, pivot: CollectorPivot)(
    implicit collectorRollersProps: Signal[CollectorRollersProperties]
  ): ContinuousTask = {
    new SpinForPurge(rollers) and new OpenCollector(clamp) and new PivotDown(pivot)
  }

  def collectUntilCubeIn(rollers: CollectorRollers, clamp: CollectorClamp, pivot: CollectorPivot)(
    implicit props: Signal[CollectorClampProps],
    hardware: CollectorClampHardware,
    collectorRollersProps: Signal[CollectorRollersProperties]
  ): FiniteTask = {
    waitForCubeIn(clamp)
      .andUntilDone(collectCube(rollers, clamp, pivot))
  }

  def waitForCubeIn(
    clamp: CollectorClamp
  )(implicit props: Signal[CollectorClampProps], hardware: CollectorClampHardware): FiniteTask = {
    new FiniteTask {
      override protected def onEnd(): Unit = Unit

      override protected def onStart(): Unit = hardware.proximitySensorReading.withCheck { it =>
        if (it > props.get.cubeGraspThreshold) {
          finished()
        }
      }
    }
  }
}
