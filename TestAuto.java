package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import android.os.Environment;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import java.util.List;

@Autonomous(name = "TestAuto", group = "Opmode RamEaters")
public class TestAuto extends LinearOpMode {
       
    private DcMotor leftWheelF = null;               //Left Wheel Front
    private DcMotor leftWheelR = null;               //Left Wheel Back
    private DcMotor rightWheelF = null;              //Right Wheel Front
    private DcMotor rightWheelR = null;
    private DcMotor slideMotor = null;
    private Servo clawLeft = null;
    private Servo clawRight = null;
    private final ElapsedTime runtime = new ElapsedTime();
    private BNO055IMU imu;
    private static final double TURN_P = 0.025;
    
    @Override
    public void runOpMode() {
        

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        leftWheelF = hardwareMap.get(DcMotor.class, "D1");
        rightWheelF = hardwareMap.get(DcMotor.class, "D2");
        leftWheelR = hardwareMap.get(DcMotor.class, "D3");
        rightWheelR = hardwareMap.get(DcMotor.class, "D4");
        
        slideMotor = hardwareMap.get(DcMotor.class, "slide");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        
        leftWheelF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftWheelR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightWheelF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightWheelR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftWheelF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftWheelR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightWheelF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightWheelR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        sleep(1000);
        
        imuInit();
        
        waitForStart();

        if (opModeIsActive()) {
            int r1 = 1;
            telemetry.addData(String.format("r1(%d)", 99999),"%d",r1);
            telemetry.update();
            
            
            caseLoc(r1);

            
            sleep(15000);
        }


       
    }


    private void move(double drive,
                      double strafe,
                      double rotate, double power, int iSleep) {

        double powerLeftF;
        double powerRightF;
        double powerLeftR;
        double powerRightR;

        powerLeftF = drive + strafe + rotate;
        powerLeftR = drive - strafe + rotate;

        powerRightF = drive - strafe - rotate;
        powerRightR = drive + strafe - rotate;
        
        leftWheelF.setPower(power);
        leftWheelR.setPower(power);
        rightWheelF.setPower(power);
        rightWheelR.setPower(power);
        
        leftWheelF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftWheelR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightWheelF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightWheelR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        leftWheelF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftWheelR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightWheelF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightWheelR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                                
        leftWheelF.setTargetPosition(leftWheelF.getCurrentPosition() + (int) (-powerLeftF));
        leftWheelR.setTargetPosition(leftWheelR.getCurrentPosition() + (int) (-powerLeftR));

        rightWheelF.setTargetPosition(rightWheelF.getCurrentPosition() + (int) (powerRightF));
        rightWheelR.setTargetPosition(rightWheelR.getCurrentPosition() + (int) (powerRightR));

        leftWheelF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftWheelR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheelF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheelR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        sleep(iSleep);

        leftWheelF.setPower(0);
        leftWheelR.setPower(0);
        rightWheelF.setPower(0);
        rightWheelR.setPower(0);

    }
    
    private void slideHigh() {
        int target = 3550;
        int max = 3550;

        if (target >= max) {
            target = max;
        }
        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
        
    }

    private void slideMid() {
        
        int target = 2700;

        int max = 2700;

        if (target >= max) {
            target = max;
        }

        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
    }

    private void slideLow() {
        int target = 750;
        int max = 750;

        if (target >= max) {
            target = max;
        }
        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
    }
    
    private void clawOpen() {

        clawLeft.setPosition(0.15);
        clawRight.setPosition(0.35);
    }

    private void clawClose() {

        clawLeft.setPosition(0);
        clawRight.setPosition(0.50);

    }
    
    private void slideDrop() {
        slideMotor.setTargetPosition(0);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
    }

    

    private void caseLoc(int loc) {
        
        gyroTurn(0);
        
        clawOpen();
        telemetry.addData("test ", "%s", "clawOpen");

        sleep(500);
        
        clawClose();
        telemetry.addData("test ", "%s", "clawClose");

        sleep(500);
        
        slideHigh();
        telemetry.addData("test ", "%s", "slideHigh");
        sleep(1000);
        
        clawOpen();
        telemetry.addData("test ", "%s", "clawOpen");
        sleep(500);
        
        slideDrop();
        telemetry.addData("test ", "%s", "slideDrop");
        sleep(2000);
        
        move(500,0,0,0.5,500);
        telemetry.addData("test ", "move (%s)", "500,0,0,0.5,500");

        move(-500,0,0,0.5,500);
        telemetry.addData("test ", "move (%s)", "-500,0,0,0.5,500");

        move(0,-500,0,0.5,500);
        telemetry.addData("test ", "move (%s)", "0,-500,0,0.5,500");

        move(0,500,0,0.5,500);
        telemetry.addData("test ", "move (%s)", "0,500,0,0.5,500");

        // call twice
        gyroTurn(0);
        gyroTurn(0);
   
        gyroTurn(90);

        move(0,0,300,0.5,500);
        telemetry.addData("test ", "move (%s)", "0,0,300,0.5,500");
        
        // call twice
        gyroTurn(0);
        gyroTurn(0);
    
        telemetry.update();
        
    }


    
    private float getHeading() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }



    private void imuInit() {

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        //parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = false;
        parameters.loggingTag = "imu";
        imu.initialize(parameters);
        
    }


    private void gyroTurn(double target_angle) {
       
        leftWheelF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftWheelR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheelF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheelR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        double currentHeading = getHeading();
        
        double delta = Math.abs((currentHeading - target_angle));
        
        int i = 0;
        int iMAX = 800;

        while (i < iMAX && opModeIsActive() && delta > 0.01) 
        {

            double error_degrees = (target_angle - currentHeading) % 360; 

            double motor_output = Range.clip(error_degrees * TURN_P, -0.6, 0.6);
            
            if (Math.abs(motor_output) < 0.020)
                i = 10001;
  
            leftWheelF.setPower(-1 * motor_output);
            leftWheelR.setPower(-1 * motor_output);
            rightWheelF.setPower(-1 * motor_output);
            rightWheelR.setPower(-1 * motor_output);
        

            currentHeading = getHeading();
            telemetry.addData("motor_output : ", motor_output);
            telemetry.addData("target_angle : ", target_angle);
            telemetry.addData("currentHeading : ", currentHeading);
            delta = Math.abs((currentHeading - target_angle));
            telemetry.addData("delta : ", delta);

            i++;
            telemetry.addData("i : ", i);
            telemetry.update();

        }

        sleep(1000);

        leftWheelF.setPower(0);
        leftWheelR.setPower(0);
        rightWheelF.setPower(0);
        rightWheelR.setPower(0);

    }
}



