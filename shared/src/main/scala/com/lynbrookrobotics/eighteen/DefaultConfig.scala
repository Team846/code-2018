package com.lynbrookrobotics.eighteen

object DefaultConfig {
  val json = """{
               |  "cubeLift": {
               |    "props": {
               |      "scaleHeight": [
               |        30,
               |        "Inches"
               |      ],
               |      "pidConfig": {
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
               |            0,
               |            "Percent"
               |          ]
               |        }
               |      },
               |      "collectHeight": [
               |        2,
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
               |        20,
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
               |        80,
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
               |        75,
               |        "Inches"
               |      ],
               |      "minHeight": [
               |        1,
               |        "Inches"
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
               |        18.8,
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
               |            1,
               |            "DegreesPerSecond"
               |          ],
               |          "num": [
               |            0,
               |            "Percent"
               |          ]
               |        }
               |      },
               |      "defaultLookAheadDistance": [
               |        2.5,
               |        "Feet"
               |      ],
               |      "maxAcceleration": [
               |        0,
               |        "FeetPerSecondSquared"
               |      ],
               |      "maxCurrent": [
               |        25,
               |        "Amperes"
               |      ],
               |      "maxRightVelocity": [
               |        19.25,
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
               |          17,
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
               |            5,
               |            "FeetPerSecond"
               |          ],
               |          "num": [
               |            0,
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
               |            1,
               |            "Degrees"
               |          ],
               |          "num": [
               |            0,
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
               |            5,
               |            "FeetPerSecond"
               |          ],
               |          "num": [
               |            0,
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
               |            0,
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
               |      ]
               |    }
               |  },
               |  "climberWinch": null,
               |  "enableLimelight": false,
               |  "led": null
               |}""".stripMargin
}