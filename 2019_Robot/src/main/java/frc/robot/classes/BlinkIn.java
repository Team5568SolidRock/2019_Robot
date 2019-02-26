/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.classes;

import edu.wpi.first.wpilibj.Spark;

/**
 * This class controls the Blinkin Lights
 */
public class BlinkIn {

    Spark BlinkIn;
    public BlinkIn(Spark blinkIn)
    {
        BlinkIn = blinkIn;
        BlinkIn.set(-0.93);
    }
}
