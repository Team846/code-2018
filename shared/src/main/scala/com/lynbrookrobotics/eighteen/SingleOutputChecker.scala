package com.lynbrookrobotics.eighteen

class SingleOutputChecker[T](hardwareName: String, get: => T) {

  private var lastOutput: Option[T] = None

  def assertSingleOutput(output: () => Unit): Unit = {
    lastOutput.filter(_ != get).foreach(_ => println(s"[ERROR] $hardwareName has been set twice!"))

    output()

    lastOutput = Some(get)
  }
}
