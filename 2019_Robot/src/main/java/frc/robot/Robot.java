/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.vision.Vision;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.DigitalInput;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import java.lang.Thread;

public class Robot extends TimedRobot {
  //Create and Initialize I2C Ports & MAX_BYTES
  //private static I2C Wire = new I2C(Port.kOnboard, 4);
	private static final int MAX_BYTES = 32;

  //Create Joysticks
  Joystick m_joystick_left;
  Joystick m_joystick_right;
  Joystick m_gamepad;

  // Create Drive Motors
  TalonSRX m_left_front;
  TalonSRX m_right_front;
  VictorSPX m_left_back;
  VictorSPX m_right_back;

  // Create Configurable Values
  NetworkTableEntry m_deadzone;

  // Create SerialPort connection
  SerialPort m_arduino;

  Double drive_left = 0.;
  Double drive_right = 0.;

  Thread Pixy;

  DigitalInput Arduino12 = new DigitalInput(0);
  DigitalInput Arduino13 = new DigitalInput(1);

  Vision Other;

  int Error;

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

    // Serial Port Initialize
    m_arduino = new SerialPort(9600, Port.kUSB);
    m_arduino.setReadBufferSize(1);

    // Configure Victors
    m_right_front.setInverted(true);
    m_right_back.setInverted(true);
    m_left_back.follow(m_left_front);
    m_right_back.follow(m_right_front);

    Other = new Vision();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   */
  @Override
  public void robotPeriodic() {
    ArduinoDrive();
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
    if(!m_gamepad.getRawButton(1)){
      tank_Drive(m_gamepad.getRawAxis(1), m_gamepad.getRawAxis(5), m_left_front, m_right_front);
    }
    else {
      if(Error > 0)
      {
        m_left_front.set(ControlMode.PercentOutput, .3);
        m_right_front.set(ControlMode.PercentOutput, .2);
      }
      else if(Error < 0)
      {
        m_left_front.set(ControlMode.PercentOutput, .2);
        m_right_front.set(ControlMode.PercentOutput, .3);
      }
      else if(Error == 0)
      {
        m_left_front.set(ControlMode.PercentOutput, .2);
        m_right_front.set(ControlMode.PercentOutput, .2);
      }
      else
      {
        m_left_front.set(ControlMode.PercentOutput, 0);
        m_right_front.set(ControlMode.PercentOutput, 0);
      }
    }
  }

  private void tank_Drive(double joystick_left_y, double joystick_right_y, TalonSRX motor_left, TalonSRX motor_right)
  {
    // Impliment Deadzone
    if(joystick_left_y < .02 && joystick_left_y > -.02)
    {
      joystick_left_y = 0;
    }
    if(joystick_right_y < .02 && joystick_right_y > -.02)
    {
      joystick_right_y = 0;
    }

    // Square joystick values
    double updated_left = -joystick_left_y * Math.abs(joystick_left_y);

    double updated_right = -joystick_right_y * Math.abs(joystick_right_y);

    // Set left values
    motor_left.set(ControlMode.PercentOutput, updated_left);
    
    // Set right values
    motor_right.set(ControlMode.PercentOutput, updated_right);
  }

  private void ArduinoDrive()
  {
    byte[] output = m_arduino.read(m_arduino.getBytesReceived());
    if(output.length == 0)
    {
      Error = 0;
    }
    else
    {
      Error = output[output.length-1];
      System.out.println(Error);
    }
  }
}