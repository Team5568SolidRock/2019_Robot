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
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

//import com.revrobotics.CANSparkMax;
//import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.classes.TankDrive;
import frc.robot.classes.PixyLineFollow;
import frc.robot.classes.SubSystems;
import frc.robot.classes.Camera;


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

  // Create Climb Motors
  Talon m_climbFront;
  Talon m_climbBack;
  Talon m_climbDrive;

  // Create Lift Motor
  //CANSparkMax m_lift;

  // Create Compressor and Solenoids
  Compressor m_compressor;
  Solenoid m_hatcher;

  //Create Custom Classes
  TankDrive m_drive;
  SubSystems m_subSystems;
  PixyLineFollow m_pixy;
  Camera m_camera;


  // Create Shuffleboard
  NetworkTableEntry m_climb_speed;
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

    // Initialize Climb Motors
    m_climbFront = new Talon(2);
    m_climbBack = new Talon(0);
    m_climbDrive = new Talon(1);

    // Initialize Lift Motor
    //m_lift = new CANSparkMax(7, MotorType.kBrushed);

    // Initialize Compressor and Solenoids
    m_compressor = new Compressor();
    m_hatcher = new Solenoid(0);

    // Configure Drive

    m_rightFront.setInverted(true);
    m_rightBack.setInverted(true);
    m_leftBack.follow(m_leftFront);
    m_rightBack.follow(m_rightFront);

    // Initialize Custom Classes
    m_drive = new TankDrive(m_leftFront, m_rightFront, .02);
    m_subSystems = new SubSystems(m_climbFront, m_climbBack, m_climbDrive, /*m_lift,*/ m_hatcher, .02);
    m_pixy = new PixyLineFollow();
    m_camera = new Camera();

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

    // Drive with joysticks or pixy
    if(!m_joystickLeft.getRawButton(1)){
      m_drive.drive(m_joystickLeft.getRawAxis(1), m_joystickRight.getRawAxis(1), 1);
    }
    else {
      m_pixy.lineFollowTalonSRX(m_leftFront, m_rightFront, .2);
    }
    // Run Climb or Lift and Hatcher Subsystems
    if(m_gamepad.getRawAxis(3) > 0.2)
    {
      // Run Climber Subsystem
      m_subSystems.climber(m_gamepad.getRawAxis(1), m_gamepad.getRawAxis(5), m_gamepad.getRawAxis(4));
    }
    else
    {
      // Run Lift Subsystem
      m_subSystems.lift(m_gamepad.getRawAxis(1));
      // Run Hatcher Subsystem
      m_subSystems.hatcher(m_gamepad.getRawButton(1));
    }

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}

