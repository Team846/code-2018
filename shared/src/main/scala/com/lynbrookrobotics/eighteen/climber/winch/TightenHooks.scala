package com.lynbrookrobotics.eighteen.climber.winch

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}
import squants.Percent

class TightenHooks(winch: ClimberWinch)(implicit props: Signal[ClimberWinchProps], hardware: ClimberWinchHardware)
    extends FiniteTask {
  override protected def onStart(): Unit = winch.setController(
    hardware.leftCurrent.zip(hardware.midCurrent).zip(hardware.rightCurrent).map {
      case ((l, m), r) =>
        if (l + m + r > props.get.climbingTotalCurrent) {
          finished()
          Percent(0)
        } else Percent(100)
    }
  )

  override protected def onEnd(): Unit = winch.resetToDefault()
}
