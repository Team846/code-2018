package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.CoreRobot

class FullAutoGenerator(r: CoreRobot)
    extends AutoGenerator(r)
    with SameSideSwitchOppositeScaleAutoGenerator
    with SameSideSwitchScaleAutoGenerator
    with OppositeSwitchSameScaleGenerator
    with OppositeSideSwitchAndScale
    with CenterSwitch
