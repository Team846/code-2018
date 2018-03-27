package com.lynbrookrobotics.eighteen.climber.winch

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}
import squants.Percent

class TightenHooks(winch: ClimberWinch)(implicit props: Signal[ClimberWinchProps], hardware: ClimberWinchHardware)
  extends FiniteTask {
  override protected def onStart(): Unit = hardware.leftCurrent.scanLeft(0) { (acc, _) => acc + 1 }.map(it =>
    if (it % 3 == 0) Percent(100) else Percent(0)
  )/*winch.setController(
    hardware.leftCurrent.zip(hardware.midCurrent).zip(hardware.rightCurrent)
      .map { case ((l, m), r) => (l + m + r) / 3 }
      .sliding(3)
      .map(_.forall { it => it > props.get.climbingTotalCurrent })
      .map(if (_) {
        finished()
        Percent(0)
      } else Percent(100))
  )*/

  override protected def onEnd(): Unit = winch.resetToDefault()

  private lazy val lowFreqDC = winch.coreTicks.scanLeft(0) { (acc, _) => acc + 1 }.map(it =>
    if (it % 2 == 0) Percent(100) else Percent(0)
  )

  private lazy val fullPower = winch.coreTicks.scanLeft(0) { (acc, _) => acc + 1 }.map(it =>
    if (it % 2 == 0) Percent(100) else Percent(0)
  )
}
