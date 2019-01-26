package org.usfirst.frc.team6962.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
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

	long start;
	RobotDrive myDrive;
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	int i = 0;
	int switchSide = 0;
	int driverStation = 1;

	double joystickLValue;
	double joystickRValue;
	double joystickArmValue;
	double joystickWheelSpeedValue;
	double solenoidOn;
	double current;
	double armMovement;
	double intakeTrigger;

	boolean joystickGripIn;
	boolean joystickGripOut;
	boolean buttonGripperIntake;
	boolean buttonGripperRelease;
	boolean enabled;
	boolean pressureSwitch;
	boolean seesGray;
	
	DigitalInput sensorIn = new DigitalInput(0);
	DoubleSolenoid exampleSolenoid = new DoubleSolenoid(0, 1);
	Compressor c = new Compressor(0);
	
	Joystick joystick0 = new Joystick(0);
	Joystick xBoxController = new Joystick(1);
	Spark spark0 = new Spark(0);
	Spark spark1 = new Spark(1);
	
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
		System.out.println("Auto selected: " + autoSelected);
		start = System.currentTimeMillis();
	}
	
	/**
	 * This function is called periodically during autonomous
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void autonomousPeriodic() {}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		c.setClosedLoopControl(true);
		
//		intakeTrigger = joystick1.getRawAxis(3);
//		armMovement = -joystick0.getRawAxis(1);
//		joystickRValue = -joystick0.getRawAxis(5);
//		joystickArmValue = -joystick1.getRawAxis(1);
//		joystickGripIn = xBoxController.getRawButton(0);
//		joystickGripOut = xBoxController.getRawButton(1);
//		solenoidOn = xBoxController.getRawAxis(3);
		enabled = c.enabled();
		pressureSwitch = c.getPressureSwitchValue();
		current = c.getCompressorCurrent();
		seesGray = sensorIn.get();
		//System.out.println(seesGray);
		
//		spark0.set(armMovement);
//		spark1.set(intakeTrigger);

		if(xBoxController.getRawButton(0)) {
			spark0.set(1.0);
			spark1.set(-1.0);
			System.out.println("Hi Nick!!!!!");
		}
		
		// For Calibration of sides
	    if(((joystickLValue > 0 && joystickRValue > 0) || (joystickLValue < 0 && joystickRValue < 0)) && (joystickLValue - joystickRValue <= 0.3 && joystickLValue - joystickRValue >= -0.3))
		{
			joystickRValue = joystickLValue;
			joystickLValue = joystickRValue;
		}
	   	if(solenoidOn >= 0.1)
		{
			exampleSolenoid.set(DoubleSolenoid.Value.kReverse);
		}
	    else
	    {
	    	exampleSolenoid.set(DoubleSolenoid.Value.kForward);
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
