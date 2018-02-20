package com.lynbrookrobotics.eighteen.collector.clamp

import squants.electro.ElectricPotential

final case class CollectorClampConfig(ports: CollectorClampPorts, props: CollectorClampProps)

final case class CollectorClampPorts(pneumaticPort: Int, proximityPort: Int)
final case class CollectorClampProps(cubeGraspThreshold: ElectricPotential)
