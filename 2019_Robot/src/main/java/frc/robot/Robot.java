/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Solenoid;

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

  // Create Solenoids
  Solenoid m_solenoid_1;
  Solenoid m_solenoid_2;

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
    m_left_front = new TalonSRX(2);
    m_right_front = new TalonSRX(1);
    m_left_back = new VictorSPX(3);
    m_right_back = new VictorSPX(4);

    // Initialize Solenoids
    m_solenoid_1 = new Solenoid(0);
    m_solenoid_2 = new Solenoid(1);

    // Configure Victors
    m_left_back.follow(m_left_front);
    m_right_back.follow(m_right_front);

    //Initialize CameraServer
    CameraServer.getInstance().startAutomaticCapture();
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
    kicker(m_solenoid_1, m_solenoid_2, m_joystick_right.getRawButton(1));
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

    //Get signs
    double left_sign = (joystick_left_y < 0) ? 1 : -1;
    double right_sign = (joystick_right_y < 0) ? 1 : -1;

    // Square joystick values
    double updated_left = -(joystick_left_y * joystick_left_y * left_sign);

    double updated_right = (joystick_right_y * joystick_right_y * right_sign);

    // Set left values
    motor_left.set(ControlMode.PercentOutput, updated_left);
    
    // Set right values
    motor_right.set(ControlMode.PercentOutput, updated_right);
  }

  private void kicker(Solenoid solenoid_1, Solenoid solenoid_2, Boolean button)
  {
    solenoid_1.set(button);
    solenoid_2.set(button);
  }
}
