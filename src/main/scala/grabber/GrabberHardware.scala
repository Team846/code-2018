package grabber
import edu.wpi.first.wpilibj.Solenoid

case class GrabberHardware(pneumatic: Solenoid)

object GrabberHardware {
  def apply(config: GrabberHardware): GrabberHardware = {
    GrabberHardware(new Solenoid(config.port.pneumatic))
  }
}