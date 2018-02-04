package grabber

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}

sealed trait GrabberState

case object GrabberOpen extends GrabberState

case object GrabberClosed extends GrabberState

class Grabber(val coreTicks: Stream[Unit])(implicit hardware: GrabberHardware)
  extends Component[GrabberState]{

  override def defaultController: Stream[GrabberState] = coreTicks.mapToConstant(GrabberOpen)

  private var curLastClosedTime: Long = 0
  val lastClosedTime = Signal(curLastClosedTime)

  override def applySignal(signal: GrabberState): Unit = {
    hardware.pneumatic.set(signal == GrabberClosed)

    if (signal == GrabberClosed) {
      curLastClosedTime = System.currentTimeMillis()
    }
  }
}