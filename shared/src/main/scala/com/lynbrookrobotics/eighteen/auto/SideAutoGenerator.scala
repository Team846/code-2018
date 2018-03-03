package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.CoreRobot
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.CollectorPivot
import com.lynbrookrobotics.eighteen.collector.pivot.PivotDown
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.{BackwardsOnly, ForwardsOnly}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.units.Point
import squants.motion.FeetPerSecondSquared
import squants.Seconds
import squants.space.{Degrees, Feet, Inches}
import squants.{Angle, Percent}


class FullAutoGenerator(r: CoreRobot) extends AutoGenerator(r)
  with SameSideSwitchOppositeScaleAutoGenerator
  with SameSideSwitchScaleAutoGenerator
  with OppositeSwitchSameScaleGenerator
  with OppositeSideSwitchAndScale
