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
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

import java.io.DataOutputStream;
import java.util.Base64;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

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

  // Create Encoders
  Encoder m_left;
  Encoder m_right;

  // Create Compressor and Solenoids
  Compressor m_compressor;
  Solenoid m_solenoid_1;
  Solenoid m_solenoid_2;

  // Create Camera
  UsbCamera m_camera;

  // Create Configurable Values
  NetworkTableEntry m_deadzone;

  // Create Shuffleboard Objects
  ShuffleboardTab m_drive_base_tab;
  ShuffleboardLayout m_encoders_layout;

  // Create SerialPort connection
  SerialPort m_arduino;

  // Create digital input
  DigitalInput ArduinoLeft;
  DigitalInput ArduinoRight;

  Double drive_left;
  Double drive_right;

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

    // Initialize Encoders
    m_left = new Encoder(1, 2, false);
    //m_right = new Encoder(3, 4, true);

    // Initialize Compressor and Solenoids
    m_compressor = new Compressor();
    m_solenoid_1 = new Solenoid(0);
    m_solenoid_2 = new Solenoid(1);

    // Configure Victors
    //m_right_front.setInverted(true);
    m_left_back.follow(m_left_front);
    m_right_back.follow(m_right_front);

    // Initialize CameraServer
    CameraServer.getInstance().startAutomaticCapture();
    CameraServer.getInstance().startAutomaticCapture();

    // Initialize Shuffleboard
    m_deadzone = Shuffleboard.getTab("Configuration").add("Joystick Deadzone", .02).withWidget("Number Slider").withPosition(1, 1).withSize(2, 1).getEntry();
    m_drive_base_tab = Shuffleboard.getTab("Drivebase");
    m_encoders_layout = m_drive_base_tab.getLayout("List Layout", "Encoders").withPosition(0, 0).withSize(2, 2);
    m_encoders_layout.add("Left Encoder", m_left);
    //m_encoders_layout.add("Right Encoder", m_right);

    // Initialize SerialPort
    m_arduino = new SerialPort(9600, SerialPort.Port.kUSB);

    // Initialize DigitalInput
    //ArduinoLeft = new DigitalInput(0);
    //ArduinoRight = new DigitalInput(1);
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
    while(true)
    {
      if(m_joystick_left.getRawButton(1))
      {
        ArduinDrive();
      }
    }
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
    if(!m_joystick_left.getRawButton(1)){
      tank_Drive(m_joystick_left.getRawAxis(1), m_joystick_right.getRawAxis(1), m_left_front, m_right_front);
    }
    else {
      ArduinDrive();
    }
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
    if(joystick_left_y < m_deadzone.getDouble(.02) && joystick_left_y > -m_deadzone.getDouble(.02))
    {
      joystick_left_y = 0;
    }
    if(joystick_right_y < m_deadzone.getDouble(.02) && joystick_right_y > -m_deadzone.getDouble(.02))
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

  private void kicker(Solenoid solenoid_1, Solenoid solenoid_2, Boolean button)
  {
    solenoid_1.set(button);
    //solenoid_2.set(button);
  }

  private void ArduinDrive()
  {
    // String drive_string = new String(m_arduino.read(12));
    
    // System.out.println("Total" + drive_string);

    // double left = drive_string.charAt(1);
    // double right = drive_string.charAt(2);

    // System.out.println("Left:" + left);
    // System.out.println("Right:" + right);

    // m_left_front.set(ControlMode.PercentOutput, m_joystick_left.getRawAxis(1));
    // m_right_front.set(ControlMode.PercentOutput, m_joystick_right.getRawAxis(1));

    // drive_left = left;
    // drive_right = right;

    // System.out.println("TEST");

    System.out.println(read());
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
		String output = new String(data);//create a string from the byte array
		int pt = output.indexOf((char)255);
		return (String) output.subSequence(0, pt < 0 ? 0 : pt);
	}
}