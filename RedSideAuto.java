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

package org.firstinspires.ftc;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

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
@Autonomous(name = "RedSideAuto", group = "Concept")
//@Disabled
public class RedSideAuto extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";


    private DcMotor leftWheelF = null;               //Left Wheel Front
    private DcMotor leftWheelR = null;               //Left Wheel Back
    private DcMotor rightWheelF = null;              //Right Wheel Front
    private DcMotor rightWheelR = null;

    private Servo clawLeft = null;
    private Servo clawRight = null;
    private DcMotor duckWheel = null;
    private DcMotor returnMotor = null;


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

    String test = "";

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();


        leftWheelF = hardwareMap.dcMotor.get("D1");
        rightWheelF = hardwareMap.dcMotor.get("D2");
        leftWheelR = hardwareMap.dcMotor.get("D3");
        rightWheelR = hardwareMap.dcMotor.get("D4");
        //color = myOpMode.hardwareMap.get(ColorSensor.class, "Color");

        clawLeft = hardwareMap.get(Servo.class, "CL");
        clawRight = hardwareMap.get(Servo.class, "CR");
        duckWheel = hardwareMap.get(DcMotor.class, "duck");
        returnMotor = hardwareMap.get(DcMotor.class, "return");


        returnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


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
            //tfod.setClippingMargins(200,150,200,150);
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        double a = getBatteryVoltage();
        telemetry.addData("voltage", a);

        telemetry.update();



        waitForStart();

        if (opModeIsActive()) {
            /*int r1 = detectRing();
            telemetry.addData(String.format("  r1 (%d)", 99999), "%d ",
                    r1);
            //hardcode for testing
            if (r1 == 1) {
                caseB();
            } else if (r1 == 4) {
                caseC();
            } else {
                caseA();
            }*/
            caseA();


        }


        if (tfod != null) {
            tfod.shutdown();
        }
    }


    private void move(double drive,
                      double strafe,
                      double rotate) {

        double powerLeftF;
        double powerRightF;
        double powerLeftR;
        double powerRightR;

        powerLeftF = drive + strafe + rotate;
        powerLeftR = drive - strafe + rotate;

        powerRightF = drive - strafe - rotate;
        powerRightR = drive + strafe - rotate;

        leftWheelF.setPower(-powerLeftF);
        leftWheelR.setPower(-powerLeftR);

        rightWheelF.setPower(powerRightF);
        rightWheelR.setPower(powerRightR);
    }

    private void slideHigh() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;
        returnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        returnMotor.setTargetPosition(returnMotor.getCurrentPosition()+800);
        returnMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        returnMotor.setPower(1);
    }
    private void slideMiddle() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;
        returnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        returnMotor.setTargetPosition(returnMotor.getCurrentPosition()+1000);
        returnMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        returnMotor.setPower(1);
    }
    private void slideLow() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;
        returnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        returnMotor.setTargetPosition(returnMotor.getCurrentPosition()+1000);
        returnMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        returnMotor.setPower(1);
    }
    private void slideDrop() {
        //drive = -gamepad1.left_stick_y;  // Negative because the gamepad is weird
        //strafe = gamepad1.left_stick_x;
        //rotate = gamepad1.right_stick_x;

        returnMotor.setPower(0);
    }

    /*private void autoShoot() {
        move(0,0,0.25);
        sleep(75);
        move(0,0,0);
        shoot(0.95);
        sleep(500);
        ringPush();
        sleep(300);
        move(0,1,0);
        sleep(500);
        move(0,0,0);
        sleep(500);
        ringPush();
        sleep(300);
        move(0,1,0);
        sleep(300);
        move(0,0,0);
        sleep(500);
        ringPush();
        sleep(300);
        shoot(0);
    } */

    private void duckSpin() {
        duckWheel.setPower(0.75);
        sleep(2000);
        duckWheel.setPower(0);
        sleep(200);
    }

    private void clawClose() {
        clawLeft.setPosition(0.5);
        clawRight.setPosition(0.5);
    }

    private void clawOpen() {
        clawLeft.setPosition(0);
        clawRight.setPosition(1);
    }

    private int detectRing() {
        while (opModeIsActive()) {
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
                        test = recognition.getLabel();
                        telemetry.addLine(test);
                        telemetry.addData(String.format("test (%d)", i), test);
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                    }
                    telemetry.update();

                    if (test.equals("Single")) {
                        telemetry.addLine("single ring");
                        telemetry.update();
                        return 1;
                    } else if (test.equals("Quad")) {
                        telemetry.addLine("quad ring");
                        telemetry.update();
                        return 4;
                    } else {
                        telemetry.addLine("nothing");
                        telemetry.update();
                        return 0;
                    }

                }
            }
        }

        return 0;
    }

    /*private void caseB() {
        sleep(3500);
        wobbleServoHand.setPosition(1);
        sleep(500);
        raiseArm(0.5);
        sleep(200);
        move(0.75, 0, 0);
        sleep(3000);
        autoShoot();
        move(0.75, 0, 0);
        sleep(2500);
        move(0, -1, 0);
        sleep(2000);
        move(0,0,0);
        sleep(300);
        lowerArm(0.5);
        sleep(300);
        lowerArm(0);
        sleep(500);
        wobbleServoHand.setPosition(0);
        sleep(200);
        raiseArm(0.5);
        sleep(300);
        raiseArm(0);
        sleep(1000);
        move(0, 0.75, 0);
        sleep(1500);
        move(-0.75,0,0);
        sleep(550);
        move(0,0,0);
    } */

    private void caseA() {
        clawClose();
        sleep(300);
        move(0,0.5,0);
        sleep(850);
        move(0,0,0);
        sleep(100);
        slideHigh();
        sleep(500);
        move(-0.45,0,0);
        sleep(950);
        move(0,0,0);
        sleep(1000);
        clawOpen();
        sleep(1000);
        move(0.3,0,0);
        sleep(1000);
        move(0,0,0);
        slideDrop();
        move(0,0,-0.45);
        sleep(950);
        move(0,0,0);
        sleep(500);
        move(0.5,0,0);
        sleep(2100);
        move(0,-0.5,0);
        sleep(750);
        move(0,0,0);
        sleep(1000);
        duckSpin();
        move(0,0.5,0);
        sleep(750);
        move(0,0,0);
    }

    /*private void caseC() {
        sleep(3500);
        wobbleServoHand.setPosition(1);
        sleep(500);
        raiseArm(0.5);
        sleep(200);
        move(0.75, 0, 0);
        sleep(3000);
        autoShoot();
        move(0.75, 0, 0);
        sleep(2500);
        move(0,0,0);
        sleep(300);
        move(0, 0, -0.5);
        sleep(1250);
        move(0.75,0,0);
        sleep(2500);
        move(0,0,0);
        sleep(300);
        lowerArm(0.5);
        sleep(300);
        lowerArm(0);
        sleep(500);
        wobbleServoHand.setPosition(0);
        sleep(200);
        raiseArm(0.5);
        sleep(300);
        raiseArm(0);
        sleep(1000);
        move(-0.75, 0, 0);
        sleep(1500);
        move(0, 0, 0.5);
        sleep(1500);
        move(-0.75,0,0);
        sleep(1250);
        move(0,0,0);
    } */

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
        tfodParameters.minimumConfidence = 0.50;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
}
