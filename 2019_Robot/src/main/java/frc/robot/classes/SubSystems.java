/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.classes;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Solenoid;

import org.junit.experimental.runners.Enclosed;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * This class runs the subsystems for the 2019 game robot.
 */
public class SubSystems {

    // Create Talon Speed Controllers
    private Talon m_climbFront;
    private Talon m_climbBack;
    private Talon m_climbDrive;

    // Create Spark Speed Controller
    private Spark m_lift;

    // Create Talon Intake Controller
    private Talon m_intake;

    // Create Solenoid
    private Solenoid m_hatcherGround;
    private Solenoid m_hatcherExtend;
    private Solenoid m_hatcherExpand;

    // Create Configurable Values
    public NetworkTableEntry m_deadzone;
    public NetworkTableEntry m_climbOffset;
    public NetworkTableEntry m_encoderValue;
    public NetworkTableEntry m_encoderScale;
    public NetworkTableEntry m_encoderHeight;
    public NetworkTableEntry m_bottomLimitSwitch;
    public NetworkTableEntry m_topLimitSwitch;
    public NetworkTableEntry m_leftBottomLimitSwitch;
    public NetworkTableEntry m_leftTopLimitSwitch;
    public NetworkTableEntry m_rightBottomLimitSwitch;
    public NetworkTableEntry m_rightTopLimitSwitch;
    public NetworkTableEntry m_liftError;
    public NetworkTableEntry m_liftTarget;
    public NetworkTableEntry m_isAutoLift;

    /**
     * This initializes all of the motors and base settings for the robot subsystems
     * @param ClimbFront The motor for the Talon front climber
     * @param ClimbBack The motor for the Talon back climber
     * @param ClimbDrive The Talon drive motor on the climber
     * @param Lift The CANSparkMax Lift Motor
     * @param Intake The intake motor
     * @param Hatcher The solenoid for the Hatcher system
     * @param defaultDeadzone The default for Switchboard deadzone value
     * @param defaultClimbOffset The default offset for the front climb bar
     * @param defaultEncoderScale The default encoder scaling value to inches.
     */
    public SubSystems(Talon ClimbFront, Talon ClimbBack, Talon ClimbDrive, Spark Lift, Talon Intake, Solenoid HatcherGround, Solenoid HatcherExtend, Solenoid HatcherExpand, double defaultDeadzone, double defaultClimbOffset, double defaultEncoderScale)
    {
        m_climbFront = ClimbFront;
        m_climbBack = ClimbBack;
        m_climbDrive = ClimbDrive;
        m_lift = Lift;
        m_intake = Intake;
        m_hatcherGround = HatcherGround;
        m_hatcherExpand = HatcherExpand;
        m_hatcherExtend = HatcherExtend;

        m_liftError.setDouble(0);
        m_liftTarget.setDouble(0);
        m_isAutoLift.setBoolean(false);

        m_deadzone = Shuffleboard.getTab("SubSystems").add("Joystick Deadzone", defaultDeadzone).withWidget(BuiltInWidgets.kNumberSlider).withPosition(2, 1).withSize(2, 1).getEntry();
        m_climbOffset = Shuffleboard.getTab("SubSystems").add("Climb Offset", defaultClimbOffset).withWidget(BuiltInWidgets.kNumberSlider).withPosition(2, 2).withSize(2, 1).getEntry();
        m_encoderValue = Shuffleboard.getTab("SubSystems").add("Encoder Value", 0).withWidget(BuiltInWidgets.kTextView).withPosition(2, 4).withSize(2, 3).getEntry();
        m_encoderScale = Shuffleboard.getTab("SubSystems").add("Encoder Scale", defaultEncoderScale).withWidget(BuiltInWidgets.kNumberSlider).withPosition(2, 5).withSize(2, 3).getEntry();
        m_encoderHeight = Shuffleboard.getTab("SubSystems").add("Encoder Height", 0).withWidget(BuiltInWidgets.kTextView).withPosition(2, 6).withSize(2, 3).getEntry();
        m_bottomLimitSwitch = Shuffleboard.getTab("SubSystems").add("Lift Bottom Limit Switch", false).withWidget(BuiltInWidgets.kTextView).withPosition(2, 6).withSize(2, 3).getEntry();
        m_topLimitSwitch = Shuffleboard.getTab("SubSystems").add("Lift Top Limit Switch", false).withWidget(BuiltInWidgets.kTextView).withPosition(2, 6).withSize(2, 3).getEntry();
        m_leftBottomLimitSwitch = Shuffleboard.getTab("SubSystems").add("Climb Left Bottom Limit Switch", false).withWidget(BuiltInWidgets.kTextView).withPosition(2, 6).withSize(2, 3).getEntry();
        m_leftTopLimitSwitch = Shuffleboard.getTab("SubSystems").add("Climb Left Top Limit Switch", false).withWidget(BuiltInWidgets.kTextView).withPosition(2, 6).withSize(2, 3).getEntry();
        m_rightBottomLimitSwitch = Shuffleboard.getTab("SubSystems").add("Climb Right Bottom Limit Switch", false).withWidget(BuiltInWidgets.kTextView).withPosition(2, 6).withSize(2, 3).getEntry();
        m_rightTopLimitSwitch = Shuffleboard.getTab("SubSystems").add("Climb Right Top Limit Switch", false).withWidget(BuiltInWidgets.kTextView).withPosition(2, 6).withSize(2, 3).getEntry();
    }

    /**
     * This runs the climb motors
     * @param joystickLeftY The left joystick value
     * @param joystickRightY The right joystick value
     * @param joystickDriveY The drive joystick value
     * @param joystickButton The button to sync front and back
     * @param leftBottomLimitSwitch Left Bottom Limit Switch
     * @param leftTopLimitSwitch Left Top Limit Switch
     * @param rightBottomLimitSwitch Right Bottom Limit Switch
     * @param rightTopLimitSwitch Right Top Limit Switch
     */
    public void climber(double joystickLeftY, double joystickRightY, double joystickDriveY, boolean joystickButton, boolean leftBottomLimitSwitch, boolean leftTopLimitSwitch, boolean rightBottomLimitSwitch, boolean rightTopLimitSwitch)
    {
        // Impliment Deadzone
        if(joystickLeftY < m_deadzone.getDouble(.02) && joystickLeftY > -m_deadzone.getDouble(.02))
        {
            joystickLeftY = 0;
        }
        if(joystickRightY < m_deadzone.getDouble(.02) && joystickRightY > -m_deadzone.getDouble(.02))
        {
            joystickRightY = 0;
        }
        if(joystickDriveY < m_deadzone.getDouble(.02) && joystickDriveY > -m_deadzone.getDouble(.02))
        {
            joystickDriveY = 0;
        }

        // Square joystick values
        double updatedLeft = joystickLeftY * Math.abs(joystickLeftY);
        double updatedRight = joystickRightY * Math.abs(joystickRightY);
        double updatedDrive = joystickDriveY * Math.abs(joystickDriveY);

        // Limit Switches
        if(updatedLeft > 0 && (leftBottomLimitSwitch || rightBottomLimitSwitch))
        {
            updatedLeft = 0;
        }
        if(updatedLeft < 0 && (leftTopLimitSwitch || rightTopLimitSwitch))
        {
            updatedRight = 0;
        }
        
        if(joystickButton)
        {
            // Set front values
            m_climbFront.set(updatedLeft * m_climbOffset.getDouble(0));
            // Set back values
            m_climbBack.set(updatedLeft);
        }
        else
        {
            // Set front values
            m_climbFront.set(updatedLeft);
            // Set back values
            m_climbBack.set(updatedRight);
        }

        // Set drive values
        m_climbDrive.set(updatedDrive);

        // Update Smartdashboard
        m_leftBottomLimitSwitch.setBoolean(leftBottomLimitSwitch);
        m_leftTopLimitSwitch.setBoolean(leftTopLimitSwitch);
        m_rightBottomLimitSwitch.setBoolean(rightBottomLimitSwitch);
        m_rightTopLimitSwitch.setBoolean(rightTopLimitSwitch);
    }

    /**
     * Runs the lift motors
     * @param joystickY The lift joystick value
     * @param joystickPov The POV of the lift joystick
     * @param encoderValue The lift current lift encoder value without scaling
     * @param liftLimitSwitchBottom Is the bottom limit switch active?
     * @param liftLimitSwitchTop Is the top limit switch active?
     */
    public void lift(double joystickY,int joystickPov, double encoderValue, boolean liftLimitSwitchBottom, boolean liftLimitSwitchTop)
    {
        // Impliment Deadzone
        if(joystickY < m_deadzone.getDouble(.02) && joystickY > -m_deadzone.getDouble(.02))
        {
            joystickY = 0;
        }

        // Square joystick values
        double updatedY = joystickY * Math.abs(joystickY);
        
        // Check bottom limit
        if(updatedY > 0 && liftLimitSwitchBottom)
        {
            updatedY = 0;
            m_liftError.setDouble(0);
        }

        // Check Top Limit
        if(updatedY < 0 && liftLimitSwitchTop)
        {
            updatedY = 0;
        }

        // Check POV
        if(joystickPov == 180)
        {
            m_liftTarget.setDouble(0);
            m_isAutoLift.setBoolean(true);
        }

        // Calculate Error
        if(m_isAutoLift.getBoolean(false) && updatedY != 0)
        {
            m_liftError.setDouble(m_liftTarget.getDouble(0));
            if(m_liftError.getDouble(0) > 5)
            {
                m_lift.set(.5);
            }
            else if(m_liftError.getDouble(0) < -5)
            {
                m_lift.set(-.5);
            }
            else{
                m_lift.set(m_liftError.getDouble(0) / 5);
            }
        }

        // Run Lift
        else
        {
            // Set motor value
            m_lift.set(updatedY);
            m_liftError.setDouble(0);
        }

        // Update Shuffleboard
        m_encoderValue.setDouble(encoderValue);
        m_encoderHeight.setDouble(encoderValue/m_encoderScale.getDouble(1));
        m_bottomLimitSwitch.setBoolean(liftLimitSwitchBottom);
        m_topLimitSwitch.setBoolean(liftLimitSwitchTop);
    }

    /**
     * Runs the intake motor
     * @param trigerLeft The trigger to intake
     * @param triggerRight The trigger to output
     */
    public void intake(double triggerLeft, double triggerRight)
    {
        if(triggerLeft > m_deadzone.getDouble(.02))
        {
            m_intake.set(triggerLeft);
        }
        else if(triggerRight > m_deadzone.getDouble(.02))
        {
            m_intake.set(-triggerRight);
        }
        else
        {
            m_intake.set(0);
        }
    }

    /**
     * Runs the hatcher solenoid
     * @param buttonGround The button to activate the ground intake solenoid
     * @param buttonExtend The button to extend the intake solenoid
     * @param buttonExpand The button to expand the intake solenoid
     */
    public void hatcher(Boolean buttonGround, Boolean buttonExtend, Boolean buttonExpand)
    {
      m_hatcherGround.set(!buttonGround);
      m_hatcherExtend.set(buttonExtend);
      m_hatcherExpand.set(buttonExpand);
    }

    /**
     * Zeros all climb motors
     */
    public void climbZero()
    {
    m_climbFront.set(0);
    m_climbBack.set(0);
    m_climbDrive.set(0);
    }

    /**
     * Zeros all lift motors
     */
    public void liftZero()
    {
        m_lift.set(0);
    }

    /**
     * Zeros intake motor
     */
    public void intakeZero()
    {
        m_intake.set(0);
    }

    /**
     * Zeros all hatcher solenoids
     */
    public void hatcherZero()
    {
        m_hatcherGround.set(false);
        m_hatcherExtend.set(false);
        m_hatcherExpand.set(false);
    }
}
