package com.lynbrookrobotics.eighteen

import com.lynbrookrobotics.potassium.streams._
import squants.time.Seconds
import squants.{Dimensionless, Quantity, Time, Velocity}

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

object StallChecker {
  def timeAboveThreshold[Q <: Quantity[Q]](stream: Stream[Q], threshold: => Q): Stream[Time] =
    stream.zipWithDt
      .scanLeft(Seconds(0)) {
        case (stallTime, (value, dt)) =>
          if (value > threshold) {
            stallTime + dt
          } else Seconds(0)
      }
}
