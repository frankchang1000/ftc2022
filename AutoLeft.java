/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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


//package org.firstinspires.ftc.teamcode;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;

/*import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;*/


import java.util.List;

/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Ultimate Goal game elements.
 * <p>
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 * <p>
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = "AutoLeft", group = "Opmode RamEaters")
//@Disabled
public class AutoLeft extends LinearOpMode {
    
    private static final String TFOD_MODEL_ASSET = String.format("%s/FIRST/tflitemodels/stationary.tflite", Environment.getExternalStorageDirectory().getAbsolutePath());


    private static final String[] LABELS = {
            "1",
            "2",
            "3"
    };
    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AXiCpJb/////AAABmUeqLpvfjkywirbDoSbnyFYKMf7uB24PIfaJZtIqcZO3L7rZVbsKVlz/fovHxEI6VgkUt3PBpXnp+YmHyLrWimMt2AKMFMYsYeZNRmz0p8jFT8DfQC7mmUgswQuPIm64qc8rxwV7vSb0et6Za96tPoDHYNHzhdiaxbI0UHpe4jCkqNTiRDFz8EVNds9kO7bCIXzxBfYfgTDdtjC5JRJ/drtM6DZnTXOqz3pdM85JEVgQqL9wBxUePSjbzyMo9e/FgxluCuWtxHraRJeeuvAlFwAb8wVAoV1cm02qIew0Vh0pDVJqy04gu62CJPhv/wwnXCKywUIEzVMbOLe7muycyHoT6ltpAn4O4s4Z82liWs9x";
    private static final double TURN_P = 0.05;
    String test = "";
    
    private DcMotor leftWheelF = null;               //Left Wheel Front
    private DcMotor leftWheelR = null;               //Left Wheel Back
    private DcMotor rightWheelF = null;              //Right Wheel Front
    private DcMotor rightWheelR = null;
    
    private DcMotor slideMotor = null;
    
    private Servo clawLeft = null;
    private Servo clawRight = null;
    
    private ColorSensor color;
    
    private int stopThreadRunning = 0;
    //private Robot_OmniDrive robot = new Robot_OmniDrive();
    private final ElapsedTime runtime = new ElapsedTime();
    private BNO055IMU imu;
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;
    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        leftWheelF = hardwareMap.get(DcMotor.class, "D1");
        rightWheelF = hardwareMap.get(DcMotor.class, "D2");
        leftWheelR = hardwareMap.get(DcMotor.class, "D3");
        rightWheelR = hardwareMap.get(DcMotor.class, "D4");
        color = hardwareMap.get(ColorSensor.class, "color");
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

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 1.78 or 16/9).

            // Uncomment the following line if you want to adjust the magnification and/or the aspect ratio of the input images.
            //tfod.setZoom(3.5, 1.78);
            //Sets the number of pixels to obscure on the left, top, right, and bottom edges of each image passed to the TensorFlow object detector. The size of the images are not changed, but the pixels in the margins are colored black.
            tfod.setZoom(2, 1280.0/852);
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        double a = getBatteryVoltage();
        telemetry.addData("voltage", a);



        telemetry.update();


        waitForStart();

        if (opModeIsActive()) {
            int r1 = detectCone();
            telemetry.addData(String.format("  r1 (%d)", 99999), "%d ",
                    r1);
            telemetry.update();
            sleep(1000);
            imuInit();

            //hardcode for testing
            //caseLoc(3);

            caseLoc(r1);



        }


        if (tfod != null) {
            tfod.shutdown();
        }
    }


    private void move(double drive,
                      double strafe,
                      double rotate, double power) {

        double powerLeftF;
        double powerRightF;
        double powerLeftR;
        double powerRightR;

        powerLeftF = drive + strafe + rotate;
        powerLeftR = drive - strafe + rotate;

        powerRightF = drive - strafe - rotate;
        powerRightR = drive + strafe - rotate;

                                
        leftWheelF.setTargetPosition(leftWheelF.getCurrentPosition() + (int) (-powerLeftF));
        leftWheelR.setTargetPosition(leftWheelR.getCurrentPosition() + (int) (-powerLeftR));

        rightWheelF.setTargetPosition(rightWheelF.getCurrentPosition() + (int) (powerRightF));
        rightWheelR.setTargetPosition(rightWheelR.getCurrentPosition() + (int) (powerRightR));

        leftWheelF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftWheelR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheelF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheelR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftWheelF.setPower(power);
        leftWheelR.setPower(power);
        rightWheelF.setPower(power);
        rightWheelR.setPower(power);
        
        
        sleep(500);

        leftWheelF.setPower(0);
        leftWheelR.setPower(0);
        rightWheelF.setPower(0);
        rightWheelR.setPower(0);

    }
    /*
    private void slideHigh() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;
        returnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        returnMotor.setTargetPosition(returnMotor.getCurrentPosition() + 720);
        returnMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        returnMotor.setPower(1);
    }

    private void slideMiddle() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;
        returnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        returnMotor.setTargetPosition(returnMotor.getCurrentPosition() + 400);
        returnMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        returnMotor.setPower(1);
    }

    private void slideLow() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;
        returnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        returnMotor.setTargetPosition(returnMotor.getCurrentPosition() + 200);
        returnMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        returnMotor.setPower(1);
    }

    private void slideDrop() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;

        returnMotor.setPower(0);
    }
    */
    
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
        //clawLeft.setPosition(0.5);
        //clawRight.setPosition(0.5);
        //close
        clawLeft.setPosition(0.09);
        clawRight.setPosition(0.41);
    }

    private void clawClose() {
        //clawLeft.setPosition(0);
        //clawRight.setPosition(1);
        //open
        clawLeft.setPosition(0);
        clawRight.setPosition(0.50);
    }
    
    private void slideDrop() {
        slideMotor.setTargetPosition(0);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
    }

    private int detectCone() {

        int iTimeOut = 5;
        int j = 0;
        Integer detect = 1;
        while (opModeIsActive() && j < iTimeOut) {
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    // step through the list of recognitions and display boundary info.
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                    
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.update();
                        detect = Integer.parseInt(recognition.getLabel());

                    }   
                }
            }
            sleep(200);
            j++;
        }
        return detect;
    }

    private void caseLoc(int loc) {
        
        clawClose();
        sleep(200);
        
        move(0,-1250,0,0.5);
        move(0,0,0,0);
        gyroTurn(0);
        sleep(100);
        gyroTurn(0);
        sleep(100);
        move(-2000,0,0,.5);
        move(-2000,0,0,.5);
        sleep(200);
        move(-650,0,0,0.4);
        sleep(500);
        gyroTurn(0);
        sleep(1000);
        gyroTurn(0);
        sleep(1000);
        move(0,0,0,0);
        move(0,650,0,0.4);
        slideHigh();
        sleep(1000);
        move(-300,0,0,0.25);
        sleep(500);
        clawOpen();
        sleep(200);
        move(250,0,0,0.25);
        slideDrop();
        sleep(200);
        gyroTurn(0);
        sleep(200);
        gyroTurn(0);
        sleep(200);
        /*move(0,0,-500,0.3);
        gyroTurn(-90);
        sleep(200);
        gyroTurn(-90);
        sleep(200);
        move(0,250,0,0.25);
        slideLow();
        move(-1400,0,0,0.4);
        move(-950,0,0,0.4);
        sleep(2000);
        clawClose();
        sleep(200);
        slideMid();
        sleep(200);
        move(0,-250,0,0.25);
        sleep(200);
        gyroTurn(-90);
        sleep(200);
        gyroTurn(-90);
        sleep(200);
        move(1400,0,0,0.5);
        move(700,0,0,0.5);
        sleep(200);
        move(0,0,500,0.3);
        sleep(200);
        gyroTurn(0);
        sleep(200);
        gyroTurn(0);
        sleep(200);
        slideHigh();
        sleep(200);
        move(-150,0,0,0.5);
        sleep(200);
        clawOpen();
        sleep(200);
        move(150,0,0,0.5);
        sleep(200);
        slideDrop();
        sleep(200);*/
        
        if(loc == 3){
            move(0,2000,0,0.8);
            sleep(2000);
            move(0,2000,0,0.55);
            sleep(2000);
            move(0,0,0,0);
        }
        
        else if(loc == 2){
            move(0,350,0,0.5);
            move(0,0,0,0);
        }
        
        else {
            move(0,-500,0,0.5);
            move(0,0,0,0);
        }

    }


    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }

    private double getFactorOfVoltage() {
        double currentVoltage = getBatteryVoltage();
        double mult;
        if (currentVoltage >= 14.6) {
            mult = 0.82;
        } else if (currentVoltage >= 14.3) {
            mult = 0.84;
        } else if (currentVoltage >= 14.2) {
            mult = 0.84;
        } else if (currentVoltage >= 14.0) {
            mult = 0.86;
        } else if (currentVoltage >= 13.6) {
            mult = 0.87;
        } else if (currentVoltage >= 13.4) {
            mult = 0.92;
        } else if (currentVoltage <= 12.5) {
            telemetry.addLine("Change the battery!");
            mult = 1;
        } else {
            mult = 0.98;
        }
        return mult;
    }


    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.3f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromFile(TFOD_MODEL_ASSET, LABELS);
        //tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
    
    private float getHeading() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
        //return 0;
    }


    /* Initializes Rev Robotics IMU */
    private void imuInit() {
        // Set up the parameters with which we will use our IMU.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = false;
        parameters.loggingTag = "imu";
        imu.initialize(parameters);
    }


    private void gyroTurn(double target_angle) {
        //double target_angle = 0 + deg;
        leftWheelF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftWheelR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheelF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheelR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry.addData("heading : ", getHeading());
        telemetry.addData("target_angle : ", target_angle);

        double currentHeading = getHeading();
        telemetry.addData("currentHeading : ", currentHeading);
        double delta = Math.abs((currentHeading - target_angle));
        telemetry.addData("delta : ", delta);

        int i = 0;
        int iMAX = 100;

        while (i < iMAX && opModeIsActive() && delta > 0.4) {

            double error_degrees = (target_angle - currentHeading) % 360; //Compute Error
            //telemetry.addData("target_angle : ",target_angle);
            //telemetry.addData("heading : ",getHeading());
            double motor_output = Range.clip(error_degrees * TURN_P, -.5, .5);
            //double test = (0 - getHeading()) % 360;//Get Correction
            //telemetry.addData("test : ",test);
            // Send corresponding powers to the motors\
            leftWheelF.setPower(-1 * motor_output);
            leftWheelR.setPower(-1 * motor_output);
            rightWheelF.setPower(-1 * motor_output);
            rightWheelR.setPower(-1 * motor_output);
            //Orientation angles = imu.getAngularOrientation (AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            //telemetry.addData("Spin Target : ",target_angle);
            //telemetry.addData("Spin Degree : ",String.format(Locale.getDefault(), "%.1f", angles.firstAngle*-1));
            //telemetry.update();
            currentHeading = getHeading();
            telemetry.addData("currentHeading : ", currentHeading);
            delta = Math.abs((currentHeading - target_angle));
            telemetry.addData("delta : ", delta);

            i++;
            telemetry.addData("i : ", i);
            telemetry.update();

        }

        sleep(500);

        leftWheelF.setPower(0);
        leftWheelR.setPower(0);
        rightWheelF.setPower(0);
        rightWheelR.setPower(0);
        //telemetry.addLine("AAAAAAA");
        //telemetry.update();

    }
}



