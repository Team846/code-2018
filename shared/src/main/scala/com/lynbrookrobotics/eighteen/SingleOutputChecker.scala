package com.lynbrookrobotics.eighteen

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
