package com.lynbrookrobotics.eighteen.drivetrain.slam

import scala.scalanative.native
import scala.scalanative.native.{CDouble, Ptr}

@native.extern
object BreezySLAM {
  def scan_alloc(): Ptr[Byte] = native.extern
  def scan_init(scan: Ptr[Byte],
                span: Int,
                size: Int,
                scanRateHz: CDouble,
                detectionAngleDegrees: CDouble,
                distanceNoDetectionMM: CDouble,
                detectionMargin: Int,
                offsetMM: Double): Unit = native.extern
}

object RunSLAM {

}