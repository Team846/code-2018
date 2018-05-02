package com.lynbrookrobotics.eighteen

object DefaultConfig {
  val json = """{
               |  "cubeLift": {
               |    "props": {
               |      "lowScaleHeight": [
               |        64,
               |        "Inches"
               |      ],
               |      "highScaleHeight": [
               |        66,
               |        "Inches"
               |      ],
               |      "maxCurrent": [
               |        30,
               |        "Amperes"
               |      ],
               |      "pidConfig": {
               |        "kd": {
               |          "den": [
               |            1,
               |            "FeetPerSecond"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "ki": {
               |          "den": [
               |            5,
               |            "Feet * s"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "kp": {
               |          "den": [
               |            0.8,
               |            "Feet"
               |          ],
               |          "num": [
               |            100,
               |            "Percent"
               |          ]
               |        }
               |      },
               |      "collectHeight": [
               |        0,
               |        "Inches"
               |      ],
               |      "voltageAtBottom": [
               |        0.277,
               |        "Volts"
               |      ],
               |      "liftPositionTolerance": [
               |        2,
               |        "Inches"
               |      ],
               |      "switchHeight": [
               |        25,
               |        "Inches"
               |      ],
               |      "exchangeHeight": [
               |        4,
               |        "Inches"
               |      ],
               |      "voltageOverHeight": {
               |        "den": [
               |          80.25,
               |          "Inches"
               |        ],
               |        "num": [
               |          1.983,
               |          "Volts"
               |        ]
               |      },
               |      "maxMotorOutput": [
               |        100,
               |        "Percent"
               |      ],
               |      "talonOverVoltage": {
               |        "den": [
               |          3.3,
               |          "Volts"
               |        ],
               |        "num": [
               |          1023,
               |          "Each"
               |        ]
               |      },
               |      "maxHeight": [
               |        83,
               |        "Inches"
               |      ],
               |      "minHeight": [
               |        0,
               |        "Inches"
               |      ],
               |      "twistyTotalRange": [
               |        0.75,
               |        "Feet"
               |      ]
               |    },
               |    "ports": {
               |      "motorPort": 20
               |    }
               |  },
               |  "forklift": {
               |    "solenoidPort": 3
               |  },
               |  "climberDeployment": {
               |    "solenoidPort": 0
               |  },
               |  "drivetrain": {
               |    "ports": {
               |      "practiceSpeedControllers": false,
               |      "rightFollowerPort": 13,
               |      "leftFollowerPort": 14,
               |      "rightPort": 11,
               |      "leftPort": 12
               |    },
               |    "props": {
               |      "maxLeftVelocity": [
               |        13,
               |        "FeetPerSecond"
               |      ],
               |      "turnVelocityGains": {
               |        "kd": {
               |          "den": [
               |            1,
               |            "DegreesPerSecond / s"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "ki": {
               |          "den": [
               |            0.017453292519943295,
               |            "Radians"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "kp": {
               |          "den": [
               |            360,
               |            "DegreesPerSecond"
               |          ],
               |          "num": [
               |            50,
               |            "Percent"
               |          ]
               |        }
               |      },
               |      "defaultLookAheadDistance": [
               |        2.5,
               |        "Feet"
               |      ],
               |      "maxAcceleration": [
               |        7,
               |        "FeetPerSecondSquared"
               |      ],
               |      "maxDeceleration": [
               |        3,
               |        "FeetPerSecondSquared"
               |      ],
               |      "maxCurrent": [
               |        35,
               |        "Amperes"
               |      ],
               |      "maxRightVelocity": [
               |        13.3,
               |        "FeetPerSecond"
               |      ],
               |      "track": [
               |        25,
               |        "Inches"
               |      ],
               |      "wheelDiameter": [
               |        6,
               |        "Inches"
               |      ],
               |      "wheelOverEncoderGears": {
               |        "num": [
               |          18,
               |          "Turns"
               |        ],
               |        "den": [
               |          74,
               |          "Turns"
               |        ]
               |      },
               |      "rightVelocityGains": {
               |        "kd": {
               |          "den": [
               |            5,
               |            "FeetPerSecondSquared"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "ki": {
               |          "den": [
               |            5,
               |            "Feet"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "kp": {
               |          "den": [
               |            4,
               |            "FeetPerSecond"
               |          ],
               |          "num": [
               |            80,
               |            "Percent"
               |          ]
               |        }
               |      },
               |      "turnPositionGains": {
               |        "kd": {
               |          "den": [
               |            0.017453292519943295,
               |            "RadiansPerSecond"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "ki": {
               |          "den": [
               |            1,
               |            "Degrees * s"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "kp": {
               |          "den": [
               |            360,
               |            "Degrees"
               |          ],
               |          "num": [
               |            150,
               |            "Percent"
               |          ]
               |        }
               |      },
               |      "blendExponent": 0,
               |      "leftVelocityGains": {
               |        "kd": {
               |          "den": [
               |            5,
               |            "FeetPerSecondSquared"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "ki": {
               |          "den": [
               |            5,
               |            "Feet"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "kp": {
               |          "den": [
               |            4,
               |            "FeetPerSecond"
               |          ],
               |          "num": [
               |            80,
               |            "Percent"
               |          ]
               |        }
               |      },
               |      "forwardPositionGains": {
               |        "kd": {
               |          "den": [
               |            5,
               |            "FeetPerSecond"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "ki": {
               |          "den": [
               |            5,
               |            "Feet * s"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        },
               |        "kp": {
               |          "den": [
               |            5,
               |            "Feet"
               |          ],
               |          "num": [
               |            50,
               |            "Percent"
               |          ]
               |        }
               |      }
               |    }
               |  },
               |  "driver": {
               |    "launchpadPort": -1,
               |    "driverWheelPort": 2,
               |    "operatorPort": 1,
               |    "driverPort": 0
               |  },
               |  "collectorClamp": {
               |    "ports": {
               |      "pneumaticPort": 2,
               |      "proximityPort": 0
               |    },
               |    "props": {
               |      "cubeGraspThreshold": [
               |        1.2,
               |        "Volts"
               |      ]
               |    }
               |  },
               |  "collectorPivot": {
               |    "pneumaticPort": 1
               |  },
               |  "collectorRollers": {
               |    "ports": {
               |      "rollerLeftPort": 1,
               |      "rollerRightPort": 0
               |    },
               |    "props": {
               |      "collectSpeed": [
               |        80,
               |        "Percent"
               |      ],
               |      "purgeSpeed": [
               |        35,
               |        "Percent"
               |      ],
               |      "purgeSpeedAuto": [
               |        60,
               |        "Percent"
               |      ],
               |      "sqrWaveFreq": [
               |        4,
               |        "Hertz"
               |      ],
               |      "sqrWaveAmpl": [
               |        35,
               |        "Percent"
               |      ]
               |    }
               |  },
               |  "climberWinch": {
               |    "ports": {
               |      "leftMotorPort": 5,
               |      "middleMotorPort": 6,
               |      "rightMotorPort": 7
               |    },
               |    "props": {
               |      "climbingSpeed": [
               |        85,
               |        "Percent"
               |      ],
               |      "sqrWaveFreq": [
               |        4,
               |        "Hertz"
               |      ],
               |      "sqrWaveAmpl": [
               |        35,
               |        "Percent"
               |      ]
               |    }
               |  },
               |  "limelight": {
               |    "cameraAngleRelativeToFront": [
               |      0,
               |      "Degrees"
               |    ],
               |    "reciprocalRootAreaToDistanceConversion": [
               |      12.0176,
               |      "Feet"
               |    ]
               |  },
               |  "led": null
               |}
               |""".stripMargin
}