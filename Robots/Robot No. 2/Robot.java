package org.usfirst.frc.team6962.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
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
	int P, I, D = 1;
	int integral, previous_error, setpoint = 0;
	
	
	long start;
	RobotDrive myDrive;
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	int i = 0;
	int switchSide = 0;
	int driverStation = 1;
	int count = 0;
	double proportional = 0.3;
	double goal = 0;
	
	double eMeasure;
	double joystickLValue;
	double joystickRValue;
	double joystickArmValue;
	boolean joystickGripIn;
	boolean joystickGripOut;
	double joystickWheelSpeedValue;
	boolean buttonGripperIntake;
	boolean buttonGripperRelease;
	double error;
	double runningSpeed = 0;

	Encoder armEncoder = new Encoder(0, 1, true, Encoder.EncodingType.k4X);
	
	Spark gripperSpark = new Spark(2);
	
	Joystick joystick0 = new Joystick(0);
	Joystick joystick1 = new Joystick(1);
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	public void PID() 
	{
		
	}
	
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
		armEncoder.setMaxPeriod(0.05);
		armEncoder.setMinRate(10);
		armEncoder.setDistancePerPulse(2.8125);
		armEncoder.setSamplesToAverage(10);
		armEncoder.reset();
		
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
		autoSelected = chooser.getSelected();	System.out.println("Auto selected: " + autoSelected);
		start = System.currentTimeMillis();
	}
	/**
	 * This function is called periodically during autonomous
	 */
	@SuppressWarnings("deprecation")
	@Override
	
	public void autonomousPeriodic() {
	}	

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		if(joystick0.getRawButton(0))
		{
			goal = 1.0;
		}
		else if(joystick0.getRawButton(1))
		{
			goal = 0.0;
		}
		else if(joystick0.getRawButton(2))
		{
			goal = 0.5;
		}
		count += armEncoder.get();
		eMeasure = armEncoder.getDistance();
		System.out.println(eMeasure);
		error = (360 * goal) - eMeasure;
		runningSpeed = (error * proportional)/360;
		
		joystickLValue = joystick0.getRawAxis(1);
		joystickRValue = joystick0.getRawAxis(1)*0.913;
		
		joystickArmValue = -joystick1.getRawAxis(1);
		joystickGripIn = joystick1.getRawButton(0);
		joystickGripOut = joystick1.getRawButton(1);
		
		gripperSpark.set(runningSpeed);
		
		// For Calibration of sides
    	if(joystick0.getRawAxis(2) < -0.1)
    	{
    		joystickLValue -= joystick0.getRawAxis(2);
    		joystickRValue += joystick0.getRawAxis(2);
    	}
    	if(joystick0.getRawAxis(2) > 0.1)
    	{
    		joystickLValue -= joystick0.getRawAxis(2);
    		joystickRValue += joystick0.getRawAxis(2);
    	}
		myDrive.tankDrive(joystickLValue, joystickRValue);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
