package com.lynbrookrobotics.eighteen.climber.winch

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Percent

class Climb(climberWinch: ClimberWinch)(implicit climberWinchProps: Signal[ClimberWinchProps]) extends ContinuousTask {
  override protected def onStart(): Unit = climberWinch.setController(
    climberWinch.coreTicks.map(_ => climberWinchProps.get.climbingSpeed)
  )

  override protected def onEnd(): Unit = climberWinch.resetToDefault()
}

class ReverseClimb(climberWinch: ClimberWinch)(implicit climberWinchProps: Signal[ClimberWinchProps])
    extends ContinuousTask {
  override protected def onStart(): Unit = climberWinch.setController(
    climberWinch.coreTicks.map(_ => -Percent(10))
  )

  override protected def onEnd(): Unit = climberWinch.resetToDefault()
}
