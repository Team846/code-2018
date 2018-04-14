package com.lynbrookrobotics.eighteen.auto

import com.lynbrookrobotics.eighteen.CoreRobot

class FullAutoGenerator(r: CoreRobot)
    extends AutoGenerator(r)
    with SameSideSwitchOppositeScaleAutoGenerator
    with SameSideScale
    with OppositeSwitchSameScaleGenerator
    with OppositeSideScale
    with OppositeSideSwitch
    with RightSwitch
    with LeftSwitch
