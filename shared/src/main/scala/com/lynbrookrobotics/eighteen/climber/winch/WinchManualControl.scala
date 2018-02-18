package com.lynbrookrobotics.eighteen.climber.winch

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Dimensionless

class WinchManualControl(target: Stream[Dimensionless])(rollers: ClimberWinch)(
  implicit props: Signal[ClimberWinchProps]
) extends ContinuousTask() {
  override protected def onStart(): Unit = rollers.setController(target)
  override protected def onEnd(): Unit = rollers.resetToDefault()
}
