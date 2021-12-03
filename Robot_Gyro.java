package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.ClassFactory;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;


public class Robot_Gyro
{
    private LinearOpMode        myOpMode;       // Access to the OpMode object
    private Robot_OmniDrive     myRobot;        // Access to the Robot hardware

    private Robot_OmniDrive robot = new Robot_OmniDrive();
    private ElapsedTime runtime = new ElapsedTime();
    private BNO055IMU imu;
    private static double TURN_P = 0.05;
    public Orientation lastAngles = new Orientation();
    public double globalAngle;
    public double correction;

    public double target_angle;
    public double error_degrees;
    public double motor_output;

    /* Constructor */
    public Robot_Gyro(){
        globalAngle = 0;
        correction = 0;
        target_angle = 0;
        error_degrees = 0;
        motor_output = 0;
    }



    public boolean gyroTurn(double degrees) {


        // Use gyro to drive in a straight line.
        correction = checkDirection();


        // rotate until turn is completed.
        while ( Math.abs(getAngle() - degrees) %360 > 0.3 && myOpMode.opModeIsActive()) {

            double error_degrees = getAngle() - degrees; //Compute Error
            double motor_output = Range.clip(error_degrees * TURN_P, -.6 ,.6); //Get Correction
            myRobot.setGyro(motor_output);

            myOpMode.telemetry.addData("1 imu heading", lastAngles.firstAngle);
            myOpMode.telemetry.addData("2 global heading", globalAngle);
            myOpMode.telemetry.addData("3 correction", correction);
            myOpMode.telemetry.addData("4 degrees", degrees);
            myOpMode.telemetry.update();

        }

        myOpMode.telemetry.addData("Gyro alredy in ", Math.abs(getAngle() - degrees) %360);
        myOpMode.telemetry.update();

        return true;
    }





    /* Initializes Rev Robotics IMU */
    public void imuInit(LinearOpMode opMode, Robot_OmniDrive robot) {

        myOpMode = opMode;
        myRobot = robot;

        // Set up the parameters with which we will use our IMU.
        imu = myOpMode.hardwareMap.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = false;
        parameters.loggingTag = "imu";
        imu.initialize(parameters);

        // restart imu movement tracking.
        resetAngle();

    }

    /**
     * Resets the cumulative angle tracking to zero.
     */
    private void resetAngle()
    {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }

}

