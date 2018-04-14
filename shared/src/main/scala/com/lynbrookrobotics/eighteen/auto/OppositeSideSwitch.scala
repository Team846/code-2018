package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.ForwardsOnly
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.units.Point
import squants.{Percent, Seconds}
import squants.motion.FeetPerSecond
import squants.space.{Feet, Inches}

import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._

trait OppositeSideSwitch extends AutoGenerator {
  import r._

  object OppositeSideSwitch {
    def justSwitchAuto(
                            drivetrain: DrivetrainComponent,
                            collectorRollers: CollectorRollers,
                            collectorClamp: CollectorClamp,
                            collectorPivot: CollectorPivot,
                            cubeLift: CubeLiftComp
                          ): FiniteTask = {
      val toScalePoints = OppositeSideSwitchScalePoints.toScalePoints
      val wayPoints = toScalePoints.take(3) ++ Seq(
        Point(
          -Inches(226.8),
          Inches(232) - Feet(1.25)
        ),
        Point(
          -Inches(259.3),
          Inches(209.6)
        ),
        Point(
          -Inches(259.3),
          Inches(195.5)
        ),
        Point(
          -Inches(245.2),
          Inches(181) - Feet(1)
        ),
        Point(
          -Inches(228.7),
          Inches(181) - Feet(1)
        )
      )

      val relativeAngle = drivetrainHardware.turnPosition.relativize((init, curr) => {
        curr - init
      })

      val pose = XYPosition
        .circularTracking(
          relativeAngle.map(compassToTrigonometric),
          drivetrainHardware.forwardPosition
        )
        .map(
          p => p + sideStartingPose
        )
        .preserve

      new FollowWayPointsWithPosition(
        wayPoints = wayPoints,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = FeetPerSecond(6),
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .andUntilDone(
          new PivotDown(collectorPivot).and(liftElevatorToSwitch(cubeLift).toContinuous)
        )
        .withTimeout(Seconds(12))
        .then(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
    }
  }
}
