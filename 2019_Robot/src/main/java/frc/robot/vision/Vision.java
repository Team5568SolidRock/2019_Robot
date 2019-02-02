/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.vision;

import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Vision {


	// These values are the default if you instantiate a PixySPI without arguments.
	// To create multiple PixySPI objects and thus use multiple Pixy cameras via SPI
	// Copy the items below, change variable names as needed and especially change
	// the SPI port used eg; Port.kOnboardCS[0-3] or Port.kMXP
	public PixyI2C pixy1;
	Port port = Port.kOnboardCS0;
	String print;
	PixyPacket packet;

	public Vision(){
		// Open a pipeline to a Pixy camera.
		pixy1 = new PixyI2C();
	}

	public void testPixy1(){
		// Get the packets from the pixy.
		try {
			packet = pixy1.readPacket(1);
		} catch (PixyException e) {
			e.printStackTrace();
		}
		
	SmartDashboard.putNumber("Pixy Vision: X: ", packet.X);
	SmartDashboard.putNumber("Pixy Vision: Y: ", packet.Y);
	SmartDashboard.putNumber("Pixy Vision: Width: ", packet.Width);
	SmartDashboard.putNumber("Pixy Vision: Height: ", packet.Height);
	}
}
