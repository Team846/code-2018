package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.lift.{CubeLiftComp, CubeLiftHardware, CubeLiftProperties}
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.lift.offloaded.OffloadedLift
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Dimensionless

package object cubeLift extends OffloadedLift {
  override type Properties = CubeLiftProperties
  override type Hardware = CubeLiftHardware
  override type Comp = CubeLiftComp
  override type LiftSignal = OffloadedSignal

  class LiftManualControl(target: Stream[Dimensionless])(lift: Comp)(implicit props: Signal[Properties])
      extends ContinuousTask() {
    override protected def onStart(): Unit = lift.setController(
      target
        .map(_ * props.get.maxMotorOutput)
        .map(openLoopToLiftSignal)
    )

    override protected def onEnd(): Unit = lift.resetToDefault()
  }
}
