
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL

class LaunchRobot extends RobotBase {

  override def startCompetition(): Unit = {

    HAL.observeUserProgramStarting()

    while (true) {
      m_ds.waitForData()
      println("To implement")
    }

  }
}
