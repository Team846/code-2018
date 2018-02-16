package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import com.lynbrookrobotics.potassium.streams.Stream
import squants.Each

class CubeLiftComp(coreTicks: Stream[Unit])(implicit hardware: CubeLiftHardware) extends Component[OffloadedSignal] {
  override def defaultController: Stream[OffloadedSignal] = coreTicks.mapToConstant(OpenLoop(Each(0)))

  override def applySignal(signal: OffloadedSignal): Unit = {
    hardware.talon.applyCommand(signal)
  }
}
