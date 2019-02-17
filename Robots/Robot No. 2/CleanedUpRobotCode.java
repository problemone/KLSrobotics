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
  * creating this project, you must also update the manifest file in the 
resource
  * directory.
  */
public class Robot extends TimedRobot{

     //time
     long start;
     //Motors
     RobotDrive myDrive;
     Spark lArmSpark;
     Spark rArmSpark;
     TalonSRX leftTalon;
     TalonSRX rightTalon;

     //Stored Values
     double joystickLValue;
     double joystickRValue;
     double joystickArmValue;
     double joystickWheelSpeedValue;

     //Joysticks
     Joystick joystick0 = new Joystick(0);
     Joystick joystick1 = new Joystick(1);

     /**
      * This function is run when the robot is first started up and 
should be used
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
     }

     /**
      * This autonomous (along with the chooser code above) shows how to 
select
      * between different autonomous modes using the dashboard. The 
sendable chooser
      * code works with the Java SmartDashboard. If you prefer the 
LabVIEW Dashboard,
      * remove all of the chooser code and uncomment the getString line 
to get the
      * auto name from the text box below the Gyro
      *
      * You can add additional auto modes by adding additional 
comparisons to the
      * switch structure below with additional strings. If using the 
SendableChooser
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

     // position_check evaluates current position against target position
     // returns true if current position is within acceptable limits
     // returns false otherwise

     @Override
     public void teleopPeriodic() {
      //Arm
	joystickArmValue = -joystick1.getRawAxis(1);
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
            //Precision mode
		boolean calebsTriggerMode = joystick0.getRawButton(1);
		 if(calebsTriggerMode)
			 myDrive.tankDrive(0.6 * joystickLValue, 0.6 * joystickRValue);
		 else
		 	myDrive.tankDrive(joystickLValue, joystickRValue);
     }

     /**
      * This function is called periodically during test mode
      */
     @Override
     public void testPeriodic() {
     }
}
