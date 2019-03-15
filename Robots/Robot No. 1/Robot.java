package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.vision.VisionThread;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import edu.wpi.first.wpilibj.smartdashboard.*;

import org.opencv.imgproc.Imgproc;

/**
  * The VM is configured to automatically run this class, and to call the
  * functions corresponding to each mode, as described in the IterativeRobot
  * documentation. If you change the name of this class or the package after
  * creating this project, you must also update the manifest file in the
resource
  * directory.
  */
public class Robot extends TimedRobot {


     int cycle = 0;

     // double integral, previous_error, setpoint = 0;
     int count = 0;
     double proportional = 1.5;
     double integral = 0.8;
     double goal = 0;


     // initial controls

     double target_position = 0;
     double current_position = 0;

     double upper_error_limit = 1;
     double lower_error_limit = 1;

    // https://wpilib.screenstepslive.com/s/currentCS/m/vision/I/674733-using-generated-code-in-a-robot-program

    // VisionThread thread;
    // final Object imgLock;


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
     private CvSource srce;
     private Mat source;
     private Mat output;

     Object sync;

     boolean hatch = false;

     //Joysticks
     Joystick joystick0 = new Joystick(0);
     Joystick joystick1 = new Joystick(1);

//     private VisionThread vThread;
    // private JFrame jframe;
    // private CvPanel panel;
    
    MyVector choice;
    
    // private BufferedImage bufImag;

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
         //encoder stuff
         armEncoder  = new Encoder(0, 1, true, Encoder.EncodingType.k4X);
         armEncoder.setMaxPeriod(0.05);
         armEncoder.setMinRate(10);
         armEncoder.setDistancePerPulse(2.8125 * 15/42);
         armEncoder.setSamplesToAverage(10);
         armEncoder.reset();

        //  panel = new CvPanel();

        //  jframe = new JFrame();
        //  jframe.setSize(840,680);
        //  jframe.setTitle("Abhinav");
        //  jframe.setContentPane(panel);
        //  jframe.setVisible(true);

         //Camera
        new Thread(() -> {
            camera = CameraServer.getInstance().startAutomaticCapture(0);
            camera.setResolution(840,680);
            sink = CameraServer.getInstance().getVideo();
            source = new Mat();
            output = new Mat();
         
            MjpegServer serv = CameraServer.getInstance().addServer("serv");
            serv.setSource(srce);

            while(!Thread.interrupted()) {
                sink.grabFrame(source);
                Imgproc.cvtColor(source,source,Imgproc.COLOR_BGR2GRAY);
                srce.putFrame(ProcessImage.displayImage(source));
                // synchronized(sync) {
                //     new ProcessImage().process(source);
                //     this.choice = ProcessImage.decision;
                // }
                // bufImag = ProcessImage.matToBufferedImage(ProcessImage.displayImage(source));
            }
         }).start();
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
         teleopPeriodic();
     }
     /**
      * This function is called periodically during autonomous
      */
     @Override

     public void autonomousPeriodic() {
         teleopPeriodic();
     }

     /**
      * This function is called periodically during operator control
      */

     public void print_status() {
      System.out.println("Cycle: " + Integer.toString(cycle));
      System.out.println("Encoder Angle: " + Double.toString(armEncoder.getDistance()));
      System.out.println("");
     }


    public void camera_thingy() {
        // double x = -999,y = -999,angle = -999;
        // synchronized(sync) {
        //     if(this.choice != null) {
        //         x = this.choice.getX();
        //         y = this.choice.getY();
        //         angle = this.choice.getAngle();
        //     }
        // }

        // //MyVector vec = new MyVector(x,y,angle);
        // System.out.println("deltaX: " + x);
        // System.out.println("deltaY: " + y);
        // System.out.println("angle: " + angle);
        //System.out.println(new MyVector(x,y,angle).toString());
    }

     @Override
     public void teleopPeriodic() {

         if (cycle % 10 == 0) {
             print_status();
         }

    //Arm Movement
     
   //Hatch Angles:
   //18.1515
   //55.7081
        //92.8853
        
        //Port Angles:
        //30.8720
        //66.5338
        //107.8915
    
    int range = 3; // degrees
    if(joystick1.getRawButton(3)) // joystick trigger
     {
         goal = 18;
         integralTracker = 0;
     }
   else if(joystick1.getRawButton(4)) // another button
     {
         goal = 45;
         integralTracker = 0;
     }
   else if(joystick1.getRawButton(6)) // another button
    {
       goal = 92;
       integralTracker = 0;
    }
   else if(joystick1.getRawButton(12))
   {
       goal = -1;
    }
    System.out.println("here: " + goal);
    goal = -1;


//          // //Encoder
    count = armEncoder.get();
    eMeasure = armEncoder.getDistance();
         //System.out.println(eMeasure);
    error = goal - eMeasure;
    System.out.println("Error: " + Double.toString(error));
    if (count % 5 == 0)
    {
      integralTracker += error;
    }
    runningSpeed = ((error * proportional)/360);
    // + ((integralTracker * integral)/360);
    System.out.println(Double.toString(runningSpeed));

    joystickArmValue = -joystick1.getRawAxis(1);
     
    if(goal != -1)
    {
      //  joystickArmValue = -runningSpeed;
      if(eMeasure < (goal - (range/2))) {
        System.out.println("eMeasure < (goal - (range / 2))");
        joystickArmValue = -0.7 * runningSpeed;
      }
      else if(eMeasure > (goal + (range/2))){
        System.out.println("eMeasure > (goal + (range / 2))");
        joystickArmValue = 0.7 * runningSpeed;
      }
     }
    
        System.out.println(Double.toString(joystickArmValue));
        System.out.println(Double.toString(0.65 * joystickArmValue));
        System.out.println("Motorsend: " + (0.65 * joystickArmValue));
        lArmSpark.set(0.65 * joystickArmValue);
        rArmSpark.set(0.65 * joystickArmValue);

//          // simple motion controls
//          // un-comment after encoder readout validation


//          // let's split this out to a separate function

//          //intake
        if(joystick1.getRawButton(1)){
            System.out.println("Button 1 was pressed!");
            joystickWheelSpeedValue = 0.85;
        } else if(joystick1.getRawButton(2)){
            System.out.println("Button 2 was pressed!");
            joystickWheelSpeedValue = -0.85;
        } else if(joystick1.getRawAxis(3) > 0) {
            System.out.println("Axis 3 was moved!");
            joystickWheelSpeedValue = -0.2;
        }
        else {
            joystickWheelSpeedValue = 0;
        }

//         SmartDashboard.putNumber("test",4.35);

        leftTalon.set(ControlMode.PercentOutput,-joystickWheelSpeedValue);
        rightTalon.set(ControlMode.PercentOutput,joystickWheelSpeedValue);

//          //Drive Train
        joystickLValue = -joystick0.getRawAxis(1) + joystick0.getRawAxis(2);
        joystickRValue = -joystick0.getRawAxis(1) - joystick0.getRawAxis(2);
        System.out.println("Goal: " + goal);
        boolean calebsTriggerMode = joystick0.getRawButton(1);
        if(calebsTriggerMode)
            myDrive.tankDrive(0.6 * joystickLValue, 0.6 * joystickRValue);
        else
            myDrive.tankDrive(0.8 * joystickLValue, 0.8 * joystickRValue);

        cycle += 1;

        // Camera output
        //camera_thingy();
     }

     /**
      * This function is called periodically during test mode
      */
     @Override
     public void testPeriodic() {
     }
}

// package frc.robot;

// import edu.wpi.first.wpilibj.Joystick;
// import edu.wpi.first.wpilibj.RobotDrive;
// import edu.wpi.first.wpilibj.Spark;
// import edu.wpi.first.wpilibj.TimedRobot;
// import com.ctre.phoenix.motorcontrol.ControlMode;
// import com.ctre.phoenix.motorcontrol.can.TalonSRX;
// import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
// import edu.wpi.first.wpilibj.CameraServer;
// import edu.wpi.cscore.CvSink;
// import edu.wpi.cscore.CvSource;
// import edu.wpi.cscore.UsbCamera;
// import edu.wpi.first.wpilibj.Encoder;
// import org.opencv.core.Mat;

// /**
//  * The VM is configured to automatically run this class, and to call the
//  * functions corresponding to each mode, as described in the IterativeRobot
//  * documentation. If you change the name of this class or the package after
//  * creating this project, you must also update the manifest file in the resource
//  * directory.
//  */
// public class Robot extends TimedRobot {
//   // control loop cycle count
//   int cycle;

//   // motor objects
//   RobotDrive drivetrain;
//   Spark arm_motor_left;
//   Spark arm_motor_right;
//   TalonSRX intake_motor_left;
//   TalonSRX intake_motor_right;

//   // encoder object
//   Encoder arm_encoder;
//   // encoder values
//   double arm_encoder_count; // number of encoder clicks
//   double arm_encoder_position; // angle position of the arm

//   // joystick objects
//   Joystick joystick_0;
//   Joystick joystick_1;

//   // joystick values
//   double joystick_0_x; // push joystick left and right
//   double joystick_0_y; // push joystick forward and backward
//   double joystick_0_z; // twist joystick clockwise, counterclockwise
//   boolean joystick_0_but1; // button 1
//   boolean joystick_0_but2; // button 2

//   double joystick_1_x;
//   double joystick_1_y;
//   double joystick_1_z;
//   boolean joystick_1_but1;
//   boolean joystick_1_but2;

//   // drivetrain control values, velocity based controls
//   double dt_left_setpoint = 0; // setpoint of the drivetrain left motor
//   double dt_left_output = 0; // current output of the drivetrain left motor
//   double dt_right_setpoint = 0; // setpoint of the drivetrain right motor
//   double dt_right_output = 0; // current output of the drivetrain right motor

//   // use same PID tuning for both motors
//   double dt_cp = 0.6; // drivetrain coeffecient of P
//   double dt_ci = 0.25; // drivetrain coefficient of I
//   double dt_cd = 0.0625; // drivetrain coefficient of D

//   double dt_left_i = 0; // drivetrain left motor integrand
//   double dt_left_d = 0; // drivetrain left motor previous value for derivative
//   double dt_left_d2 = 0; // drivetrain left motor previous value for derivative

//   double dt_right_i = 0; // drivetrain right motor integrand
//   double dt_right_d = 0; // drivetrain right motor previous value for derivative
//   double dt_right_d2 = 0; // drivetrain right motor previous value for derivative

//   // error reduces oscillation near target point
//   double dt_error = 0.05; // drivetrain acceptable error
//   double dt_idle_speed = 0; // drivetrain idle speed

//   // arm control values, velocity based controls
//   double arm_setpoint = 0; // setpoint of the arm
//   double arm_output = 0; // output of the arm

//   double arm_cp = 0.6; // arm coefficient of P
//   double arm_ci = 0.25; // arm coefficient of I
//   double arm_cd = 0.0625; // arm coefficient of D

//   double arm_i = 0; // arm integrand
//   double arm_d = 0; // arm previous value for derivative
//   double arm_d2 = 0; // arm previous value for derivative

//   // sets the maximum value allowed by the arm position encoder
//   double arm_position_max = 135;
//   double arm_position_error = 0;
//   double arm_idle_speed = 0; // arm idle speed; arm holds position with no motion
//   boolean arm_manual_control = true; // flag for cycle count
//   double arm_joystick_input_error = 0.05;

//   // intake control values, velocity based controls
//   double it_setpoint = 0;
//   double it_output = 0;

//   double it_cp = 0.6; // intake coefficient of P
//   double it_ci = 0.25; // intake coefficient of I
//   double it_cd = 0.0625; // intake coefficient of D
  
//   double it_i = 0; // intake integrand
//   double it_d = 0; // intake previous value for derivative
//   double it_d2 = 0; // intake prevoius value for derivative

//   double it_error = 0.01; // acceptable error on intake
//   double it_idle_speed = 0.2; // intake idle speed
//   double it_on_speed = 0.85; // intake on speed

//   UsbCamera camera;
//   Mat source;
//   Mat output;
//   CvSink sink;
//   CvSource srce;



//   /**
//    * This function is run when the robot is first started up and should be used
//    * for any initialization code.
//    */
//   @Override
//   public void robotInit() {
//     // initialize drivetrain object
//     drivetrain = new RobotDrive(0, 1);
    
//     // initialize arm objects
//     // motors are connected via differential, should be driven together
//     arm_motor_left = new Spark(2);
//     arm_motor_right = new Spark(3);

//     // initialize intake objects
//     intake_motor_left = new TalonSRX(0);
//     intake_motor_right = new TalonSRX(1);

//     // initialize joystick objects
//     joystick_0 = new Joystick(0);
//     joystick_1 = new Joystick(1);

//     joystick_0_x= 0;
//     joystick_0_y = 0;
//     joystick_0_z = 0;
//     joystick_0_but1 = false;
//     joystick_0_but2 = false;
//     joystick_1_x = 0;
//     joystick_1_y = 0;
//     joystick_1_z = 0;
//     joystick_1_but1 = false;
//     joystick_1_but2 = false;


//     // initialize encoder objects
//     arm_encoder  = new Encoder(0, 1, true, Encoder.EncodingType.k4X);
//     arm_encoder.setMaxPeriod(0.05);
//     arm_encoder.setMinRate(10);
//     arm_encoder.setDistancePerPulse(2.8125 * 15/42);
//     arm_encoder.setSamplesToAverage(10);
//     arm_encoder.reset();

//     System.out.println("Initialized");

// //     //          //Camera
// //  camera = CameraServer.getInstance().startAutomaticCapture();
// //  camera.setResolution(840,680);
// //  sink = CameraServer.getInstance().getVideo();
// //  source = new Mat();
// //  output = new Mat();

//   }


 
//   /**
//    * This autonomous (along with the chooser code above) shows how to select
//    * between different autonomous modes using the dashboard. The sendable chooser
//    * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
//    * remove all of the chooser code and uncomment the getString line to get the
//    * auto name from the text box below the Gyro
//    *
//    * You can add additional auto modes by adding additional comparisons to the
//    * switch structure below with additional strings. If using the SendableChooser
//    * make sure to add them to the chooser code above as well.
//    */
//   @Override
//   public void autonomousInit() {
//     //start = System.currentTimeMillis();
//   }
//   /**
//    * This function is called periodically during autonomous
//    */
//   @Override
  
//   public void autonomousPeriodic() {
//   }



//   // prints current status to console to monitor controls
//   public void print_status() {
//     // current iteration of the control loop
//     System.out.println("Cycle: " + Integer.toString(cycle));
//     System.out.println("");

//     // drivetrain status
//     System.out.println("Drivetrain L - Current Output: " + Double.toString(dt_left_output));
//     System.out.println("Drivetrain L - Target Output: " + Double.toString(dt_left_setpoint));

//     System.out.println("Drivetrain R - Current Output: " + Double.toString(dt_right_output));
//     System.out.println("Drivetrain R - Target Output: " + Double.toString(dt_right_setpoint));

//     // need to add values for the joystick inputs
//     System.out.println("Drivetrain - Joystick - X: " + Double.toString(joystick_0_x));
//     System.out.println("Drivetrain - Joystick - Y: " + Double.toString(joystick_0_y));
//     System.out.println("");

//     System.out.println("Arm - Current Output: " + Double.toString(arm_output));
//     System.out.println("Arm - Target Output: " + Double.toString(arm_setpoint));

//     // need to add values for the joystick inputs
//     System.out.println("Arm - Joystick - Y: " + Double.toString(joystick_1_y));
//     System.out.println("");

//     System.out.println("Intake - Current Output: " + Double.toString(it_output));
//     System.out.println("Intake - Target Output: " + Double.toString(it_setpoint));
//     System.out.println("");
//     // SmartDashboard.put_number("Joystick 0 - X", joystick_0_x);
//     // SmartDashboard.put_number("Joystick 0 - Y", joystick_0_y);
//     // SmartDashboard.put_number("Joystick 0 - Z", joystick_0_z);
//     // SmartDashboard.put_boolean("Joystick 0 - Button 0", joystick_0_but1);
//     // SmartDashboard.put_boolean("Joystick 0 - Button 1", joystick_0_but2);

//     // SmartDashboard.put_number("Joystick 1 - X", joystick_1_x);
//     // SmartDashboard.put_number("Joystick 1 - Y", joystick_1_y);
//     // SmartDashboard.put_number("Joystick 1 - Z", joystick_1_z);
//     // SmartDashboard.put_boolean("joystick 1 - Button 0", joystick_1_but1);
//     // SmartDashboard.put_boolean("Joystick 1 - Button 1", joystick_1_but2);


//   }


//   public void use_camera_system() {
//     sink.grabFrame(source);
//     srce.putFrame(ProcessImage.displayImage(source));
//     MyVector choice = ProcessImage.processImage(source);
//     System.out.println(choice);
//   }


//   // check if value is within error of target value
//   public boolean check_target(double value, double target, double error) {
//     double upper_limit = target + error;
//     double lower_limit = target - error;

//     if (value > lower_limit && value < upper_limit) {
//       return true;
//     }
//     else {
//       return false;
//     }
//   }



//   // check if value is within error of 0
//   public boolean check_target_zero(double value, double error) {
//     return check_target(value, 0, error);
//   }



//   // reads and stores joystick values at each cycle
//   public void get_input() {
//     // joystick 0
//     // joystick 0 x not used
//     joystick_0_x = 0;
//     // joystick 0 y is drivetrain forward/backward
//     joystick_0_y = joystick_0.getRawAxis(1);
//     // joystick 0 z is twist motion, drivetrain left or right
//     joystick_0_z = joystick_0.getRawAxis(2);

//     // joystick 1
//     // joystick 1 x not used
//     joystick_1_x = 0;
//     // joystick 1 y is arm up/down
//     joystick_1_y = joystick_1.getRawAxis(1);
//     // intake in
//     joystick_1_but1 = joystick_1.getRawButton(1);
//     // intake out
//     joystick_1_but2 = joystick_1.getRawButton(2);

//     // encoder
//     arm_encoder_count = arm_encoder.get();
//     arm_encoder_position = arm_encoder.getDistance();
//   }



//   // determine intake output
//   // intake motor mapping
//   public void calc_intake() {
//     // if no button press, set intake to idle speed
//     if (joystick_1_but1 == false && joystick_1_but2 == false) {
//       it_setpoint = it_idle_speed;
//     }
//     // button 1 press; set to turn inwards
//     else if (joystick_1_but1 == true && joystick_1_but2 == false) {
//       it_setpoint = it_on_speed;
//     }
//     // button 2 press; set to turn outwards
//     else if (joystick_1_but1 == false && joystick_1_but2 == true) {
//       it_setpoint = it_on_speed * -1;
//     }
//     // both button press; set to idle speed as a safety precaution
//     else if (joystick_1_but1 == true && joystick_1_but2 == true) {
//       it_setpoint = it_idle_speed;
//     }
//     // should never reach, set speed to 0
//     else {
//       it_setpoint = 0;
//     }
//   }



//   // update motor output for intake setpoint
//   public void update_output_intake() {
//     double it_cycle_output; // instantiate value for intake motors; assume symmetry on intake motors

//     // calculate current cycle output values
//     double it_error = it_setpoint - it_output;
//     double it_cycle_p = it_error * it_cp; // multiply error by P constant
//     double it_cycle_i = it_i * it_ci; // multiply integral of error by I constant
//     double it_cycle_d = (it_d2 - it_d)  * it_cd; // multiply derivative of error by D constant
    
//     // calculate output value
//     it_cycle_output = it_cycle_p + it_cycle_i + it_cycle_d;
    
//     // update values for next cycle
//     it_i = it_i + it_error; // add error to integrand for new integral
//     it_d2 = it_d;
//     it_d = it_error;

//     intake_motor_right.set(ControlMode.PercentOutput, it_cycle_output);
//     intake_motor_left.set(ControlMode.PercentOutput, it_cycle_output * -1);
//   }



//   public void calc_arm() {
//     arm_setpoint = joystick_1_y;

//     // NEED TO IMPLEMENT
//     // if button press; change to position based setpoint until joystick is actuated again, reset integrand and derivative values upon toggle
//   }



//   public void update_output_arm() {
//     if (false) {
//       // NEED TO IMPLEMENT TOGGLE
//       // if button press and no joystick input
//       return;
//     }
//     else {
//       double arm_cycle_output; // instantiate value for intake motors; assume symmetry on intake motors

//       // calculate current cycle output values
//       double arm_error = arm_setpoint - arm_output;
//       double arm_cycle_p = arm_error * arm_cp; // multiply error by P constant
//       double arm_cycle_i = arm_i * arm_ci; // multiply integral of error by I constant
//       double arm_cycle_d = (arm_d2 - arm_d)  * arm_cd; // multiply derivative of error by D constant
      
//       // calculate output value
//       arm_cycle_output = arm_cycle_p + arm_cycle_i + arm_cycle_d;
      
//       // update values for next cycle
//       arm_i = arm_i + arm_error; // add error to integrand for new integral
//       arm_d2 = arm_d;
//       arm_d = arm_error;

//       arm_motor_right.set(arm_cycle_output);
//       arm_motor_left.set(arm_cycle_output);
//     }
//   }



//   // calculates output for drivetrain motors based on joystick input
//   public void calc_drivetrain() {
//     // forward and backward control with the joystick, reversed for direction
//     double drivetrain_move = joystick_0_y * -1;
//     // turn left and right control
//     double drivetrain_turn = joystick_0_z;

//     // tank drive calculation
//     double drivetrain_left = drivetrain_move + drivetrain_turn;
//     double drivetrain_right = drivetrain_move - drivetrain_turn;

//     // sets the setpoints for the motors
//     dt_left_setpoint = drivetrain_left;
//     dt_right_setpoint = drivetrain_right;
//   }



//   // use new output for drivetrain motors
//   // simple PID control
//   public void update_output_drivetrain() {
//     double drivetrain_left_cycle_output; // instantiate value for left motor output value
//     double drivetrain_right_cycle_output; // instantiate value for right motor output value

//     // calculate current cycle output values
//     double dt_left_error = dt_left_setpoint - dt_left_output;
//     double dt_left_cycle_p = dt_left_error * dt_cp; // multiply error by P constant
//     double dt_left_cycle_i = dt_left_i * dt_ci; // multiply integral of error by I constant
//     double dt_left_cycle_d = (dt_left_d2 - dt_left_d)  * dt_cd; // multiply derivative of error by D constant
    
//     // calculate end value
//     drivetrain_left_cycle_output = dt_left_cycle_p + dt_left_cycle_i + dt_left_cycle_d;
    
//     // update P, I, D values for next cycle
//     dt_left_output = drivetrain_left_cycle_output; // log new output value
//     dt_left_i = dt_left_i + dt_left_error; // add error to integrand
//     dt_left_d2 = dt_left_d; // set past point to calc derivative for next cycle
//     dt_left_d = dt_left_error; // set past point to calc derivative for next cycle

//     // calculate current cycle output values
//     double dt_right_error = dt_right_setpoint - dt_right_output;
//     double dt_right_cycle_p = dt_right_error * dt_cp; // multiply error by P constant
//     double dt_right_cycle_i = dt_right_i * dt_ci; // multiply integral of error by I constant
//     double dt_right_cycle_d = (dt_right_d2 - dt_right_d) * dt_cd; // multiply derivative of error by D constant
    
//     // calculate end value
//     drivetrain_right_cycle_output = dt_right_cycle_p + dt_right_cycle_i + dt_right_cycle_d;
    
//     // update values for next cycle
//     dt_right_output = drivetrain_right_cycle_output;
//     dt_right_i = dt_right_i + dt_right_error;
//     dt_right_d2 = dt_right_d;
//     dt_right_d = dt_right_error;

//     drivetrain.tankDrive(drivetrain_left_cycle_output, drivetrain_right_cycle_output);
//   }



//   // increments values needed for the next cycle
//   public void increment_cycle() {
//     cycle += 1;
//   }



//   // loop function for the robot
//   @Override
//   public void teleopPeriodic() {
//     // prints current status to roboRIO console
//     if (cycle % 20 == 0) {
//       print_status();
//     }
//     // collects input from controllers and updates values
//     System.out.println("Get Joystick Input");
//     get_input();
//     // calculates appropriate drivetrain output
//    calc_drivetrain();
//     // calculates appropriate arm output
//     calc_arm();
//     // calculates appropriate intake output
//     calc_intake();
//     // PID control on drivetrain
//     update_output_drivetrain();
//     // PID control on arm
//     update_output_arm();
//     // PID control on intake
//     update_output_intake();
//     // increment value for the next cycle
//     increment_cycle();
//     // using camera system
//     // use_camera_system();
//   }



//   /**
//    * This function is called periodically during test mode
//    */
//   @Override
//   public void testPeriodic() {
//   }
// }
