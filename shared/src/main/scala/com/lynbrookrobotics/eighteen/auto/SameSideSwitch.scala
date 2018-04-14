package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClamp
import com.lynbrookrobotics.eighteen.collector.pivot.{CollectorPivot, PivotDown}
import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.eighteen.lift.CubeLiftComp
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.eighteen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit.ForwardsOnly
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.units.Point
import squants.{Angle, Percent}
import squants.space.Inches
import squants.time.Seconds

trait SameSideSwitch extends AutoGenerator {
  import r._
  object SameSideSwitch {
    def dropOffToSwitch(
                         drivetrain: DrivetrainComponent,
                         collectorRollers: CollectorRollers,
                         collectorClamp: CollectorClamp,
                         collectorPivot: CollectorPivot,
                         cubeLift: CubeLiftComp,
                         pose: Stream[Point],
                         relativeAngle: Stream[Angle]
                       ): FiniteTask = {
      new FollowWayPointsWithPosition(
        wayPoints = SameSideSwitchOppositeScalePoints.toSwitchPoints,
        tolerance = Inches(6),
        position = pose,
        turnPosition = relativeAngle,
        maxTurnOutput = Percent(100),
        cruisingVelocity = purePursuitCruisingVelocity,
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly
      )(drivetrain)
        .andUntilDone(new PivotDown(collectorPivot).and(liftElevatorToSwitch(cubeLift).toContinuous))
        .then(
          dropCubeSwitch(collectorRollers, collectorClamp, collectorPivot, cubeLift)
        )
    }

    def justSwitchAuto(
                        drivetrain: DrivetrainComponent,
                        collectorRollers: CollectorRollers,
                        collectorClamp: CollectorClamp,
                        collectorPivot: CollectorPivot,
                        cubeLift: CubeLiftComp
                      ): FiniteTask = {
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

      dropOffToSwitch(drivetrain, collectorRollers, collectorClamp, collectorPivot, cubeLift, pose, relativeAngle)
        .withTimeout(Seconds(5))
    }
  }

}
