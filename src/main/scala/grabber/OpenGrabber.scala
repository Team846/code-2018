package grabber

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class OpenGrabber(grabber: Grabber) extends ContinuousTask {
  override protected def onStart(): Unit = {
    grabber.setController(grabber.coreTicks.mapToConstant(GrabberClosed))
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}