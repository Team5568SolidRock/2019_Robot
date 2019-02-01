/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

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
  VictorSPX m_left_back;
  VictorSPX m_right_back;

  // Create Climb
  Talon m_climb_front;
  Talon m_climb_back;
  Talon m_climb_drive;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {

    // Initialize Joysticks
    m_joystick_left = new Joystick(0);
    m_joystick_right = new Joystick(1);
    m_gamepad = new Joystick(2);

    // Initialize Drive Motors
    m_left_front = new TalonSRX(7);
    m_right_front = new TalonSRX(9);
    m_left_back = new VictorSPX(6);
    m_right_back = new VictorSPX(8);

    // Initialize Climb Motors
    m_climb_front = new Talon(2);
    m_climb_back = new Talon(0);
    m_climb_drive = new Talon(1);
    
    // Configure Victors
    m_left_front.setInverted(true);
    m_left_back.setInverted(true);
    m_left_back.follow(m_left_front);
    m_right_back.follow(m_right_front);
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
    tank_Drive(m_joystick_left.getRawAxis(1), m_joystick_right.getRawAxis(1), m_left_front, m_right_front);
    climb(m_gamepad.getRawAxis(1), m_gamepad.getRawAxis(5), m_gamepad.getRawAxis(2), m_gamepad.getRawAxis(3), m_gamepad.getRawButton(1), m_climb_back, m_climb_front, m_climb_drive);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  private void tank_Drive(double joystick_left_y, double joystick_right_y, TalonSRX motor_left, TalonSRX motor_right)
  {
    // Impliment Deadzone
    if(joystick_left_y < DEADZONE && joystick_left_y > -DEADZONE)
    {
      joystick_left_y = 0;
    }
    if(joystick_right_y < DEADZONE && joystick_right_y > -DEADZONE)
    {
      joystick_right_y = 0;
    }

    // Square joystick values
    double updated_left = joystick_left_y * Math.abs(joystick_left_y);

    double updated_right = joystick_right_y * Math.abs(joystick_right_y);

    // Set left values
    motor_left.set(ControlMode.PercentOutput, updated_left);

    // Set right values
    motor_right.set(ControlMode.PercentOutput, updated_right);
  }

  private void climb(double gamepad_left_y, double gamepad_right_y, double gamepad_left_trigger, double gamepad_right_trigger, boolean gamepad_button_a, Talon climb_back, Talon climb_front, Talon climb_drive)
  {
    // Impliment Deadzone
    if(gamepad_left_y < DEADZONE && gamepad_left_y > -DEADZONE)
    {
      gamepad_left_y = 0;
    }
    if(gamepad_right_y < DEADZONE && gamepad_right_y > -DEADZONE)
    {
      gamepad_right_y = 0;
    }
    if(gamepad_left_trigger < DEADZONE && gamepad_left_trigger > -DEADZONE)
    {
      gamepad_left_trigger = 0;
    }
    if(gamepad_right_trigger < DEADZONE && gamepad_right_trigger > -DEADZONE)
    {
      gamepad_right_trigger = 0;
    }

    // Square joystick values
    double updated_left = gamepad_left_y * Math.abs(gamepad_left_y);

    double updated_right = gamepad_right_y * Math.abs(gamepad_right_y);

    double updated_left_trigger = gamepad_left_trigger * Math.abs(gamepad_left_trigger);

    double updated_right_trigger = gamepad_right_trigger * Math.abs(gamepad_right_trigger);


    // Set front and back values
    if(gamepad_button_a)
    {
      climb_front.set(updated_left * .9);
      climb_back.set(updated_left);
    }
    else
    {
      climb_front.set(updated_left);
      climb_back.set(updated_right);
    }

    // Set drive values
    if(updated_left_trigger > 0)
    {
      climb_drive.set(updated_left_trigger);
    }
    else if(updated_right_trigger > 0)
    {
      climb_drive.set(updated_right_trigger);
    }
    else
    {
      climb_drive.set(0);
    }
  }
}
