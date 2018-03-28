package com.lynbrookrobotics.eighteen.drivetrain.slam

import edu.wpi.first.wpilibj.SerialPort
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.wpilibj.hal.SerialPortJNI
import squants.space.{Length, Millimeters}

class XVLidar(port: SerialPort.Port) {
  val serial = new SerialPort(115200, port)

  val startBytesBuffer = new Array[Byte](1)
  def readSingleByte: Byte = {
    SerialPortJNI.serialRead(port.value.toByte, startBytesBuffer, 1)
    startBytesBuffer(0)
  }
  private def readStartBytes(): Unit = {
    while((readSingleByte & 0xFF) != 0xFA) {}
    while ((readSingleByte & 0xFF) != 0xA0) {}
  }

  def timed[T](f: String)(thunk: => T): T = {
    val start = System.currentTimeMillis()
    val ret = thunk
    println(s"$f: took ${System.currentTimeMillis() - start}")
    ret
  }

  serial.writeString("MotorOn\r\n")
  serial.writeString("SetRPM 300\r\n")

  private val (stream, pub) = Stream.manual[Seq[Length]]
  new Thread(new Runnable {
    override def run(): Unit = {
      val buffer = new Array[Byte](1980 - 2)
      val retRanges = new Array[Length](360)
      while (true) {
        try {
          readStartBytes()
          val remainingBytes = {
            // use the same buffer unlike wpilib
            SerialPortJNI.serialRead(port.value.toByte, buffer, 1980 - 2)
            buffer
          } // remaining data, we already read the first two bytes

          // total of ((2 /* crc */ + 2 /* rpm */ + 16 /* 4 readings of 4 bytes */ + 2 /* random data? */) = 22) * 90 = 1980
          def getNthByte(i: Int): Int = {
            if (i == 0) 0xFA
            else if (i == 1) 0xA0
            else remainingBytes(i - 2) & 0xFF
          }

          (0 until 90).foreach { setI =>
            val startI = setI * 22
            val rpms = ((getNthByte(startI + 3) << 8) | getNthByte(startI + 2)).toDouble / 64
            (1 to 4).foreach { distI =>
              val byte0 = getNthByte(startI + (distI * 4))
              val byte1 = getNthByte(startI + (distI * 4) + 1)
              val byte2 = getNthByte(startI + (distI * 4) + 2)
              val byte3 = getNthByte(startI + (distI * 4) + 3)

              val range = ((byte1 & 0x3F) << 8) + byte0
              val intensity = (byte3 << 8) + byte2

              retRanges((setI * 4) + (distI - 1)) = Millimeters(range)
            }
          }

          pub(retRanges)
        } catch {
          case t: Throwable => t.printStackTrace()
        }
      }
    }
  }).start()

  val frames = stream
}
