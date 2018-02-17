package com.lynbrookrobotics.eighteen.climber.winch

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class Climb(climberWinch: ClimberWinch)(implicit climberWinchProps: Signal[ClimberWinchProps]) extends ContinuousTask {
  override protected def onStart(): Unit = climberWinch.setController(
    climberWinch.coreTicks.map(_ => climberWinchProps.get.climbingSpeed)
  )

  override protected def onEnd(): Unit = climberWinch.resetToDefault()
}
