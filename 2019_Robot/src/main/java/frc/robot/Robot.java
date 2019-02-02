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
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import java.lang.Thread;

public class Robot extends TimedRobot {
  //Create and Initialize I2C Ports & MAX_BYTES
  private static I2C Wire = new I2C(Port.kOnboard, 4);
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
    m_left_front = new TalonSRX(2);
    m_right_front = new TalonSRX(1);
    m_left_back = new VictorSPX(3);
    m_right_back = new VictorSPX(4);

    // Configure Victors
    m_left_back.follow(m_left_front);
    m_right_back.follow(m_right_front);

    // Thread Pixy = new Thread(new Runnable() {
    //   @Override
    //   public void run() {
    //     for(int x = 0; x < 10; x++)
    //     {
    //       ArduinDrive();
    //     }
    //   }
    // });
    // Pixy.start();

    Other = new Vision();
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
    if(!m_gamepad.getRawButton(1)){
      tank_Drive(m_gamepad.getRawAxis(1), m_gamepad.getRawAxis(5), m_left_front, m_right_front);
    }
    else {
      Other.testPixy1();
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

    double updated_right = joystick_right_y * Math.abs(joystick_right_y);

    // Set left values
    motor_left.set(ControlMode.PercentOutput, updated_left);
    
    // Set right values
    motor_right.set(ControlMode.PercentOutput, updated_right);
  }

  private void ArduinDrive()
  {
    String error = read();
    try {
      Thread.sleep(100, 0);
    } catch (Exception sleepInterupteException) {
      System.out.println("sleepInterruptException");
    }
    System.out.println("Error:" + error);
    if(!error.equals("none") && !error.equals(""))
    {
      try {
        int drive_string = Integer.parseInt(error);
      } catch (Exception parseIntException) {
        System.out.println("Parsing the int is what broke");
      }
      int drive_string = Integer.parseInt(error);
      System.out.println("Drive Int:" + drive_string);

      double left;
      double right;
      if(drive_string > 0)
      {
        left = 1;
        right = .5;
      }
      else if(drive_string < 0)
      {
        left = .5;
        right = 1;
      }
      else
      {
        left = .5;
        right = .5;
      }

      drive_left = left;
      drive_right = right;
    }
  }

  public void write(String input){//writes to the arduino 
    char[] CharArray = input.toCharArray();//creates a char array from the input string
    byte[] WriteData = new byte[CharArray.length];//creates a byte array from the char array
    for (int i = 0; i < CharArray.length; i++) {//writes each byte to the arduino
      WriteData[i] = (byte) CharArray[i];//adds the char elements to the byte array 
    }
    Wire.transaction(WriteData, WriteData.length, null, 0);//sends each byte to arduino
  }

  private String read(){//function to read the data from arduino
		byte[] data = new byte[MAX_BYTES];//create a byte array to hold the incoming data
		Wire.read(4, MAX_BYTES, data);//use address 4 on i2c and store it in data
    System.out.println(data);
    String output = new String(data);//create a string from the byte array
    System.out.println(output);
    int pt = output.indexOf((char)255);
		return (String) output.subSequence(0, pt < 0 ? 0 : pt);
	}
}