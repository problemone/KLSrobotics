package org.usfirst.frc.team6962.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	long start;
	RobotDrive myDrive;
	RobotDrive armDrive;
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	int i = 0;

	double joystickLValue;
	double joystickRValue;
	double joystickArmValue;
	double joystickWheelSpeedValue;
	boolean buttonGripperIntake;
	boolean buttonGripperRelease;
	

	Joystick joystick0 = new Joystick(0);
	Joystick joystick1 = new Joystick(1);
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		myDrive = new RobotDrive(0, 1);
		armDrive = new RobotDrive(2);
	}
 
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		start = System.currentTimeMillis();
	}
	/**
	 * This function is called periodically during autonomous
	 */
	@SuppressWarnings("deprecation")
	@Override
	
	public void autonomousPeriodic() {
		long current = System.currentTimeMillis();
		if(current - start <= 3000) {
			myDrive.arcadeDrive(0.75,0.1);
		}else if(current - start >= 3000 && current - start < 4000/*this number is variable*/) {
			myDrive.arcadeDrive(1,0);
		}
		else
		{
			myDrive.arcadeDrive(0,0);
		}	
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		/*
		joystickRValue = joystickL.getRawAxis(3) - joystickL.getRawAxis(2);
		joystickLValue = joystickL.getRawAxis(3) - joystickL.getRawAxis(2);
		if(joystickL.getRawAxis(0) > 0)
		{
			if(joystickL.getRawAxis(3) >= joystickL.getRawAxis(2))
			{
				joystickLValue += joystickL.getRawAxis(0);
				joystickRValue -= joystickL.getRawAxis(0);
			}
			else if(joystickL.getRawAxis(3) < joystickL.getRawAxis(2))
			{
				joystickLValue -= joystickL.getRawAxis(0);
				joystickRValue += joystickL.getRawAxis(0);
			}
		}
		else if(joystickL.getRawAxis(0) < 0)
		{
			if(joystickL.getRawAxis(3) >= joystickL.getRawAxis(2))
			{
				joystickRValue -= joystickL.getRawAxis(0);
				joystickLValue += joystickL.getRawAxis(0);
			}
			else if(joystickL.getRawAxis(3) < joystickL.getRawAxis(2))
			{
				joystickRValue += joystickL.getRawAxis(0);
				joystickLValue -= joystickL.getRawAxis(0);
			}
		}*/
		joystickLValue = -joystick0.getRawAxis(1);
		joystickRValue = -joystick0.getRawAxis(5);
		joystickArmValue = -joystick1.getRawAxis(1);
		joystickWheelSpeedValue = -joystick1.getRawAxis(3)+1;
		buttonGripperIntake = joystick1.getRawButton(1);
		buttonGripperRelease = joystick1.getRawButton(2);
		
		if(((joystickLValue > 0 && joystickRValue > 0) || (joystickLValue < 0 && joystickRValue < 0)) && (joystickLValue - joystickRValue <= 0.3 && joystickLValue - joystickRValue >= -0.3))
		{
			
				joystickRValue = joystickLValue*(1+0.04/0.5);
				joystickLValue = joystickRValue;
		}
		myDrive.tankDrive(joystickLValue, joystickRValue);
		myDrive.arcadeDrive(joystickArmValue,0);
		myDrive.tankDrive((joystickArmUpValue/100)*30,(joystickArmUpValue/100)*30);
		if(buttonGripperIntake){
			myDrive.tankDrive(joystickWheelSpeedValue,joystickWheelSpeedValue);
		} else if(buttonGripperRelease){
			myDrive.tankDrive(-joystickWheelSpeedValue,-joystickWheelSpeedValue)
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
