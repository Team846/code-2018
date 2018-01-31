package com.lynbrookrobotics.eighteen.climber

import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream

class Climb(climberWinch: ClimberWinch)(implicit climberWinchConfig: Stream[ClimberWinchConfig]) extends ContinuousTask{
  override protected def onStart(): Unit = climberWinch.setController(climberWinchConfig.map(_.climbingSpeed))

  override protected def onEnd(): Unit = climberWinch.resetToDefault()
}
