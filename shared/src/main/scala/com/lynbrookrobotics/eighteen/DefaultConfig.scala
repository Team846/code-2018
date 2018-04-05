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
               |        74,
               |        "Inches"
               |      ],
               |      "minHeight": [
               |        0,
               |        "Inches"
               |      ],
               |      "twistyTotalRange": [
               |        0.75,
               |        "Feet"
               |      ],
               |      "maxCurrentDraw": [
               |        20,
               |        "Amperes"
               |      ],
               |      "stallTimeout": [
               |        3,
               |        "Seconds"
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
               |      "leftPort": 12,
               |      "rightFollowerPdpPort": 3,
               |      "leftFollowerPdpPort": 1,
               |      "rightPdpPort": 2,
               |      "leftPdpPort": 0
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
               |        5,
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
               |      "parallelMotorCurrentThreshold" : [
               |        5,
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
               |      "leftFudge": 1.08,
               |      "rightFudge": 1.08,
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
               |            80,
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
               |      },
               |      "deltaVelocityStallThreshold": [
               |        10,
               |        "FeetPerSecond"
               |      ],
               |      "stallTimeout": [
               |        3,
               |        "Seconds"
               |      ]
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
               |        50,
               |        "Percent"
               |      ],
               |      "purgeSpeed": [
               |        70,
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