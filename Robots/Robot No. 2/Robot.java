package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.Encoder;
import org.opencv.core.Mat;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
	
	// double integral, previous_error, setpoint = 0;
	int count = 0;
	double proportional = 0.3;
	double integral = 0.2;
	double goal = 0;
	double gripperMovement;
	double eMeasure;
	boolean joystickGripIn;
	boolean joystickGripOut;
	boolean buttonGripperIntake;
	boolean buttonGripperRelease;
	double error;
	double runningSpeed = 0;
	int integralTracker = 0;
	
	//time
	long start;
	//Motors
	RobotDrive myDrive;
	Spark lArmSpark;
	Spark rArmSpark;
	TalonSRX leftTalon;
	TalonSRX rightTalon;

	//Encoder
	Encoder armEncoder;

	//Stored Values
	double joystickLValue;
	double joystickRValue;
	double joystickArmValue;
	double joystickWheelSpeedValue;

	//Camera
	private UsbCamera camera;
	private CvSink sink;
	private Mat source;

	//Joysticks
	Joystick joystick0 = new Joystick(0);
	Joystick joystick1 = new Joystick(1);

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Drive Train
		myDrive = new RobotDrive(0, 1);
		//Arm
		lArmSpark = new Spark(2);
		rArmSpark = new Spark(3);
		//Intake
		leftTalon = new TalonSRX(0);
		rightTalon = new TalonSRX(1);
		//encoder stuff
		armEncoder  = new Encoder(0, 1, true, Encoder.EncodingType.k4X);
		armEncoder.setMaxPeriod(0.05);
		armEncoder.setMinRate(10);
		armEncoder.setDistancePerPulse(2.8125 * 15/42);
		armEncoder.setSamplesToAverage(10);
		armEncoder.reset();
		//Camera
		// camera = CameraServer.getInstance().startAutomaticCapture();
		// camera.setResolution(840,680);
		// sink = CameraServer.getInstance().getVideo();
		// source = new Mat();
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
		start = System.currentTimeMillis();
	}
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	
	public void autonomousPeriodic() {
	}	

	/**
	 * This function is called periodically during operator control
	 */

	

	@Override
	public void teleopPeriodic() {
		//Arm Movement
		if(joystick1.getRawButton(1))
		{
			goal = 30;
			integralTracker = 0;
		}
		else if(joystick1.getRawButton(2))
		{
			goal = 0;
			integralTracker = 0;
		}
		else if(joystick1.getRawButton(3))
		{
			goal = 15;
			integralTracker = 0; 
		}
		else if(joystick1.getRawButton(12)){
			goal = -2;
		}
		goal = -2;
		
		// //Encoder
		count = armEncoder.get();
		eMeasure = armEncoder.getDistance();
		//System.out.println(eMeasure);
		error = goal - eMeasure;
		if(count % 5 == 0)
		{
			integralTracker += error;
		}
		runningSpeed = ((error * proportional)/360) + ((integralTracker * integral)/360);

		//Arm
		joystickArmValue = -joystick1.getRawAxis(1);
		if(goal != -2)
		{
			joystickArmValue = -runningSpeed;
		}
		lArmSpark.set(joystickArmValue);
		rArmSpark.set(joystickArmValue);

		//intake
		if(joystick1.getRawButton(1)){
			joystickWheelSpeedValue = 0.85;
		}else if(joystick1.getRawButton(2)){
			joystickWheelSpeedValue = -0.85;
		}else{
			joystickWheelSpeedValue = -0.2;
		}

		leftTalon.set(ControlMode.PercentOutput,-joystickWheelSpeedValue);
		rightTalon.set(ControlMode.PercentOutput,joystickWheelSpeedValue);

		//Drive Train
		joystickLValue = -joystick0.getRawAxis(1) + joystick0.getRawAxis(2);
		joystickRValue = -joystick0.getRawAxis(1) - joystick0.getRawAxis(2);
		myDrive.tankDrive(joystickLValue, joystickRValue);
		
		//Camera
		// sink.grabFrame(source);
		// MyVector choice = ProcessImage.processImage(source);
		// return choice.getAngle();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
