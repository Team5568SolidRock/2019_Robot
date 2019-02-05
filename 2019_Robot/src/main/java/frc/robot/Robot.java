/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import frc.robot.classes.TankDrive;
import frc.robot.classes.PixyLineFollow;

public class Robot extends TimedRobot {

  //Create Joysticks
  Joystick m_joystickLeft;
  Joystick m_joystickRight;
  Joystick m_gamepad;

  // Create Drive Motors
  TalonSRX m_leftFront;
  TalonSRX m_rightFront;
  VictorSPX m_leftBack;
  VictorSPX m_rightBack;

  // Create Compressor and Solenoids
  Compressor m_compressor;
  Solenoid m_solenoid1;

  // Create Camera
  UsbCamera m_camera;

  // Create Configurable Values
  NetworkTableEntry m_deadzone;

  //Create Custom Classes
  TankDrive m_drive;
  PixyLineFollow m_pixy;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    // Initialize Joysticks
    m_joystickLeft = new Joystick(0);
    m_joystickRight = new Joystick(1);
    m_gamepad = new Joystick(3);

    // Initialize Drive Motors
    m_leftFront = new TalonSRX(2);
    m_rightFront = new TalonSRX(1);
    m_leftBack = new VictorSPX(3);
    m_rightBack = new VictorSPX(4);

    // Initialize Compressor and Solenoids
    m_compressor = new Compressor();
    m_solenoid1 = new Solenoid(0);

    // Configure Drive
    m_rightFront.setInverted(true);
    m_rightBack.setInverted(true);
    m_leftBack.follow(m_leftFront);
    m_rightBack.follow(m_rightFront);

    // Initialize CameraServer
    CameraServer.getInstance().startAutomaticCapture();

    // Initialize Shuffleboard
    m_deadzone = Shuffleboard.getTab("Configuration").add("Joystick Deadzone", .02).withWidget("Number Slider").withPosition(1, 1).withSize(2, 1).getEntry();

    // Initialize Custom Classes
    m_drive = new TankDrive(m_leftFront, m_rightFront);
    m_pixy = new PixyLineFollow();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This function is called when autonomous is first started.
   */
  @Override
  public void autonomousInit() {
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if(!m_joystickLeft.getRawButton(1)){
      m_drive.drive(m_joystickLeft.getRawAxis(1), m_joystickRight.getRawAxis(1), 1);
    }
    else {
      m_pixy.lineFollowTalonSRX(m_leftFront, m_rightFront, .2);
    }
    kicker(m_solenoid1, m_joystickRight.getRawButton(1));
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  private void kicker(Solenoid solenoid1, Boolean button)
  {
    solenoid1.set(button);
  }
}