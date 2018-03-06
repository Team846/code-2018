package com.lynbrookrobotics.eighteen

object DefaultConfig {
  val json = """{
               |  "cubeLift": {
               |    "props": {
               |      "lowScaleHeight": [
               |        66,
               |        "Inches"
               |      ],
               |      "highScaleHeight": [
               |        68,
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
               |        0.5,
               |        "Inches"
               |      ],
               |      "voltageAtBottom": [
               |        0.316,
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
               |          2.5,
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
               |        73,
               |        "Inches"
               |      ],
               |      "minHeight": [
               |        0.25,
               |        "Inches"
               |      ],
               |      "twistyTotalRange": [
               |        1.5,
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
               |        17.7,
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
               |      "maxCurrent": [
               |        35,
               |        "Amperes"
               |      ],
               |      "maxRightVelocity": [
               |        17.7,
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
               |            50,
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
               |      "rollerLeftPort": 0,
               |      "rollerRightPort": 1
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
               |  "climberWinch": null,
               |  "enableLimelight": false,
               |  "limelight": {
               |    "cameraAngleRelativeToFront": [
               |      0,
               |      "Degrees"
               |    ],
               |    "reciprocalRootAreaToDistanceConversion": [
               |      12.0176,
               |      "Feet"
               |    ]
               |  }
               |  "led": null
               |}""".stripMargin
}