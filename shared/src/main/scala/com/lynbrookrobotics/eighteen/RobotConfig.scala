package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.eighteen.collector.rollers.CollectorRollersConfig
import com.lynbrookrobotics.eighteen.collector.clamp.CollectorClampConfig
import com.lynbrookrobotics.eighteen.driver.DriverConfig
import com.lynbrookrobotics.eighteen.drivetrain.DrivetrainConfig

case class RobotConfig(driver: DriverConfig,
                       drivetrain: DrivetrainConfig,
                       collectorRollers: CollectorRollersConfig,
                       collectorClamp: CollectorClampConfig)
