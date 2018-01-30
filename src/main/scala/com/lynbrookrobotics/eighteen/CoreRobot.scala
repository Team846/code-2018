package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent

class CoreRobot (configFileValue: Stream[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])
                (implicit val config: Stream[RobotConfig], hardware: RobotHardware,
                 val clock: Clock, val polling: ImpulseEvent){

}
