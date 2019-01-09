/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Joystick;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Robot extends TimedRobot {

  //Constants
  final double DEADZONE = .02;

  //Create Joysticks
  Joystick m_joystick_left;
  Joystick m_joystick_right;
  Joystick m_gamepad;

  // Create Drive Motors

  TalonSRX m_left_front;
  TalonSRX m_right_front;
  Talon m_left_back;
  Talon m_right_back;

  // Initialize Drive Motors
  DifferentialDrive m_drive;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    // Initialize Joysticks
    m_joystick_left = new Joystick(0);
    m_joystick_right = new Joystick(1);
    m_gamepad = new Joystick(3);

    // Initialize Drive Motors
    m_left_front = new TalonSRX(0);
    m_right_front = new TalonSRX(1);
    m_left_back = new Talon(2);
    m_right_back = new Talon(3);

    // Configure Talons

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
    tank_Drive(m_joystick_right.getY(), m_joystick_left.getY(), m_left_front, m_right_front, m_left_back, m_right_back);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  private void tank_Drive(double joystick_left_y, double joystick_right_y, TalonSRX motor_front_left, TalonSRX motor_front_right, Talon motor_back_left, Talon motor_back_right)
  {
    // Impliment Deadzone
    if(joystick_left_y < DEADZONE)
    {
      joystick_left_y = 0;
    }
    if(joystick_right_y < DEADZONE)
    {
      joystick_right_y = 0;
    }

    // Square joystick values
    double updated_left = (joystick_left_y * joystick_left_y) / 2;

    double updated_right = (joystick_right_y * joystick_right_y) / 2;

    // Set left values
    motor_front_left.set(ControlMode.PercentOutput, updated_left);
    motor_back_left.set(updated_left);

    // Set right values
    motor_front_right.set(ControlMode.PercentOutput, updated_right);
    motor_back_right.set(updated_right);
  }
}
