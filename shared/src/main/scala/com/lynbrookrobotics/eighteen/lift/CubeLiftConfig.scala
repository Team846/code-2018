package com.lynbrookrobotics.eighteen.lift

final case class CubeLiftConfig(
  ports: CubeLiftPorts,
  props: CubeLiftProperties,
  idx: Int,
  timeout: Int
)
