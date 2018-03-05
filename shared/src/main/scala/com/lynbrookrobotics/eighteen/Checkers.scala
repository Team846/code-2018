package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.potassium.streams._
import squants.time.Seconds
import squants.{Dimensionless, Time, Velocity}

class SingleOutputChecker[T](hardwareName: String, get: => T) {
  private var lastOutput: Option[T] = None

  def assertSingleOutput(output: => Unit): Unit = {
    val currentOutput = get
    lastOutput
      .filter(_ != currentOutput)
      .foreach(
        it =>
          println(
            s"""[ERROR] $hardwareName has been set twice!
         |[ERROR]   -> expected $it, but set to $currentOutput""".stripMargin
        )
      )

    output

    lastOutput = Some(get)
  }
}

class StallChecker(deltaVelocityStallThreshold: => Velocity, maxVelocity: => Velocity) {
  def checkStall(velocityAndDc: Stream[(Velocity, Dimensionless)]): Stream[Time] =
    velocityAndDc.map { case (actual, dc) => (actual, maxVelocity * dc.toEach) }.map {
      case (actual, expected) => expected - actual
    }.zipWithDt
      .scanLeft(Seconds(0)) {
        case (stallTime, (velocityDiff, dt)) =>
          if (velocityDiff > deltaVelocityStallThreshold) {
            stallTime + dt
          } else Seconds(0)
      }
}
