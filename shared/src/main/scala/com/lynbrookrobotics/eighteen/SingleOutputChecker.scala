package com.lynbrookrobotics.eighteen

class SingleOutputChecker[T](hardwareName: String, get: => T) {

  private var lastOutput: Option[T] = None

  def assertSingleOutput(output: => Unit): Unit = {
    val currentOutput = get
    lastOutput.filter(_ != currentOutput).foreach(_ => println(
      s"""[ERROR] $hardwareName has been set twice!
         |[ERROR]   -> expected ${lastOutput.get}, but set to $currentOutput""".stripMargin
    ))

    output

    lastOutput = Some(get)
  }
}
