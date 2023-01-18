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

@Autonomous(name = "AutoR", group = "Opmode RamEaters")
public class AutoR extends LinearOpMode {

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

    private static final String TFOD_MODEL_ASSET = String.format("%s/FIRST/tflitemodels/stationary.tflite", Environment.getExternalStorageDirectory().getAbsolutePath());


    private static final String[] LABELS = {
            "1",
            "2",
            "3"
    };
    private static final String VUFORIA_KEY =
            "AXiCpJb/////AAABmUeqLpvfjkywirbDoSbnyFYKMf7uB24PIfaJZtIqcZO3L7rZVbsKVlz/fovHxEI6VgkUt3PBpXnp+YmHyLrWimMt2AKMFMYsYeZNRmz0p8jFT8DfQC7mmUgswQuPIm64qc8rxwV7vSb0et6Za96tPoDHYNHzhdiaxbI0UHpe4jCkqNTiRDFz8EVNds9kO7bCIXzxBfYfgTDdtjC5JRJ/drtM6DZnTXOqz3pdM85JEVgQqL9wBxUePSjbzyMo9e/FgxluCuWtxHraRJeeuvAlFwAb8wVAoV1cm02qIew0Vh0pDVJqy04gu62CJPhv/wwnXCKywUIEzVMbOLe7muycyHoT6ltpAn4O4s4Z82liWs9x";

    String test = "";
    private VuforiaLocalizer vuforia;
    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

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



    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.6f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromFile(TFOD_MODEL_ASSET, LABELS);
        //tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
    private int detectCone() {

        int iTimeOut = 5;
        int j = 0;
        Integer detect = 2;
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


              // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

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
            tfod.setZoom(1, 1280.0/852);
            tfod.setClippingMargins(250,150,250,150);
        }

        sleep(1000);

        imuInit();

        waitForStart();

        if (opModeIsActive()) {
            int r1 = detectCone();
            telemetry.addData(String.format("r1(%d)", 99999),"%d",r1);
            telemetry.update();


            caseLoc(r1);


            //sleep(15000);
        }

        if (tfod != null) {
            tfod.shutdown();
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

        clawClose();

        sleep(300);

        slideLow();

        sleep(200);

        move(0,1080,0,0.3,1000);

        gyroTurn(3);

        move(-2300,20,0,0.2,1750);

        gyroTurn(3);

        move(-2320,20,0,0.2,1800);

        gyroTurn(-1);

        move(0,-720,0,0.3,680);

        gyroTurn(0);

        slideHigh();

        sleep(1500);

        move(-250,0,0,0.25,500);

        gyroTurn(0);

        clawOpen();

        sleep(200);

        move(300,0,0,0.25,500);

        slideDrop();

        sleep(1000);




        if(loc == 3){

            move(-100,0,0,0.3,500);

            gyroTurn(2);

            move(0,-2000,0,0.5,2500);
        }

        else if(loc == 2){
            gyroTurn(3);
            move(0,-650,0,0.5,1000);

        }

        else {
            gyroTurn(3);
            move(0,500,0,0.5,500);

        }


        clawOpen();
        sleep(500);
        slideDrop();
        sleep(1500);
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
        int iMAX = 400;

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
