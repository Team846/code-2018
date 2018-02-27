package com.lynbrookrobotics.eighteen.lift

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import com.lynbrookrobotics.potassium.streams.Stream
import squants.{Each, Percent}
import squants.electro.Volts

class CubeLiftComp(val coreTicks: Stream[Unit])(implicit hardware: CubeLiftHardware)
    extends Component[OffloadedSignal] {
  override def defaultController: Stream[OffloadedSignal] = coreTicks.mapToConstant(OpenLoop(Each(0)))

  override def applySignal(signal: OffloadedSignal): Unit = {
    if(hardware.readPotentiometerVoltage() == Volts(0) && !signal.isInstanceOf[OpenLoop]) {
      hardware.talon.applyCommand(OpenLoop(Percent(0)))
      println("NO CUBE LIFT POTENTIOMETER DETECTED")
    } else {
      hardware.talon.applyCommand(signal)
    }
  }
}
