/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import frc.robot.classes.PixyLineFollow;
import frc.robot.classes.TankDrive;

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

  // Create Custom Classes
  PixyLineFollow m_pixy;
  TankDrive m_drive;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    // Initialize Joysticks
    m_joystickLeft = new Joystick(0);
    m_joystickRight = new Joystick(1);
    m_gamepad = new Joystick(2);

    // Initialize Drive Motors
    m_leftFront = new TalonSRX(7);
    m_rightFront = new TalonSRX(9);
    m_leftBack = new VictorSPX(6);
    m_rightBack = new VictorSPX(8);

    // Initialize Custom Classes
    m_pixy = new PixyLineFollow();
    m_drive = new TankDrive(m_leftFront, m_rightFront);

    // Configure Victors
    m_rightFront.setInverted(true);
    m_rightBack.setInverted(true);
    m_leftBack.follow(m_leftFront);
    m_rightBack.follow(m_rightFront);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   */
  @Override
  public void robotPeriodic() {
    m_pixy.arduinoRead();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if(!m_gamepad.getRawButton(1)){
      m_drive.drive(m_gamepad.getRawAxis(1), m_gamepad.getRawAxis(5), 1.);
    }
    else {
      m_pixy.lineFollowTalonSRX(m_leftFront, m_rightFront, .2);
    }
  }
}