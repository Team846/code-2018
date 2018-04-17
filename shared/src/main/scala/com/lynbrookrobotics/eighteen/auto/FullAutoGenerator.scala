package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.CoreRobot

class RightAutoGenerator(r: CoreRobot)
    extends AutoGenerator(r, startFromLeft = false)
    with SameSideSwitchOppositeScaleAutoGenerator
    with SameSideScale
    with OppositeSwitchSameScaleGenerator
    with OppositeSideScale
    with OppositeSideSwitch

class LeftAutoGenerator(r: CoreRobot)
    extends AutoGenerator(r, startFromLeft = true)
    with SameSideSwitchOppositeScaleAutoGenerator
    with SameSideScale
    with OppositeSwitchSameScaleGenerator
    with OppositeSideScale
    with OppositeSideSwitch

class CenterAutoGenerator(r: CoreRobot) extends AutoGenerator(r, true) with RightSwitch with LeftSwitch
