package org.firstinspires.ftc.teamcode.s2021;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.ColorSensor;


//@Disabled
@TeleOp(name = "ftc_code12021", group = "Opmode RamEaters")

public class Ftc_code1 extends OpMode {

    private final ElapsedTime runtime = new ElapsedTime();
    // Declare Hardware
    private DcMotor leftWheelF = null;               //Left Wheel Front
    private DcMotor leftWheelR = null;               //Left Wheel Back
    private DcMotor rightWheelF = null;              //Right Wheel Front
    private DcMotor rightWheelR = null;
    private int random = 10;
    private int count = 0;
    private DcMotor slideMotor = null;

    private Servo clawLeft = null;
    private Servo clawRight = null;

    private ColorSensor color;
    private int stopThreadRunning = 0;

    @Override
    public void init() {

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftWheelF = hardwareMap.get(DcMotor.class, "D1");
        rightWheelF = hardwareMap.get(DcMotor.class, "D2");
        leftWheelR = hardwareMap.get(DcMotor.class, "D3");
        rightWheelR = hardwareMap.get(DcMotor.class, "D4");
        color = hardwareMap.get(ColorSensor.class, "color");
        slideMotor = hardwareMap.get(DcMotor.class, "slide");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        runtime.reset();

        //sleep(1000);
        move();
    }

    @Override
    public void start() {
        runtime.reset();
    }

    private void move() {
        double drive;
        // Power for forward and back motion
        double strafe;  // Power for left and right motion
        double rotateLeft;
        double rotateRight;// Power for rotating the robot
        //int intake;
        //double linearSlide;
        drive = gamepad1.left_stick_y;  // Negative because the gamepad is weird
        strafe = gamepad1.left_stick_x;
        rotateLeft = gamepad1.right_trigger;
        rotateRight = gamepad1.left_trigger;
        //linearSlide = gamepad2.left_stick_y;
        //intake = gamepad2.left_trigger;

        double powerLeftF;
        double powerRightF;
        double powerLeftR;
        double powerRightR;
        //double slidePower;
        // double powerIntake;
        //intakeWheel1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //intakeWheel1.setPower(1);

        //if full power on left stick
        powerLeftF = drive - strafe + rotateRight - rotateLeft;
        powerLeftR = drive + strafe + rotateRight - rotateLeft;
        //powerIntake = intake;
        powerRightF = drive + strafe - rotateRight + rotateLeft;
        powerRightR = drive - strafe - rotateRight + rotateLeft;

        //slidePower = linearSlide;

        //slideMotor.setPower(slidePower);
        if (stopThreadRunning == 0) {
            leftWheelF.setPower(-powerLeftF * 0.6);
            leftWheelR.setPower(-powerLeftR * 0.6);

            rightWheelF.setPower(powerRightF * 0.6);
            rightWheelR.setPower(powerRightR * 0.6);
        }
        /*
        //intakeWheel1.setPower(powerIntake);
        if (poleDetect()) {
            if (stopThreadRunning == 0) {
                Thread stopThread = new stopThread();
                stopThread.start();
            }
        }
        */

        if (gamepad2.right_bumper) {
            //open
            clawLeft.setPosition(0);
            clawRight.setPosition(0.50);
        } else if (gamepad2.left_bumper) {
            //close
            clawLeft.setPosition(0.09);
            clawRight.setPosition(0.41);
        }

        if (gamepad2.y) {
            slideHigh();
        }
        if (gamepad2.b) {
            slideMid();
        }
        if (gamepad2.a) {
            slideLow();
        }
        if (gamepad2.x) {
            slideDrop();
        }
        if (gamepad2.dpad_up) {
            slideAdd(200);
        }
        if (gamepad2.dpad_down) {
            slideSubtract(200);
        }
    }

    private void slideHigh() {

        //slideMotor.setDirection(DcMotor.Direction.FORWARD);
        //slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if (random % 2 == 0) {
            slideMotor.setDirection(DcMotor.Direction.FORWARD);
        } else {
            slideMotor.setDirection(DcMotor.Direction.REVERSE);
        }
        int target = 3500;
        int max = 3500;

        if (target >= max) {
            target = max;
        }
        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
    }

    private void slideMid() {

        //slideMotor.setDirection(DcMotor.Direction.FORWARD);
        //slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if (random % 2 == 0) {
            slideMotor.setDirection(DcMotor.Direction.FORWARD);
        } else {
            slideMotor.setDirection(DcMotor.Direction.REVERSE);
        }
        int target = 2600;

        int max = 2600;

        if (target >= max) {
            target = max;
        }

        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
    }

    private void slideLow() {
        //slideMotor.setDirection(DcMotor.Direction.FORWARD);
        //slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if (random % 2 == 0) {
            slideMotor.setDirection(DcMotor.Direction.FORWARD);
        } else {
            slideMotor.setDirection(DcMotor.Direction.REVERSE);
        }
        int target = 1600;
        int max = 1600;



        if (target >= max) {
            target = max;
        }
        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);


    }

    private void slideSubtract(int sub) {

        //slideMotor.setDirection(DcMotor.Direction.FORWARD);
        if (random % 2 == 0) {
            slideMotor.setDirection(DcMotor.Direction.REVERSE);
        } else {
            slideMotor.setDirection(DcMotor.Direction.FORWARD);
        }

        //slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int target = slideMotor.getCurrentPosition() + sub;

        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(0.5);
    }

    private void slideAdd(int add) {

        //slideMotor.setDirection(DcMotor.Direction.FORWARD);
        if (random % 2 == 0) {
            slideMotor.setDirection(DcMotor.Direction.FORWARD);
        } else {
            slideMotor.setDirection(DcMotor.Direction.REVERSE);
        }

        //slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int target = slideMotor.getCurrentPosition() + add;

        if (target >= 3600) {
            target = 3600;
        }
        slideMotor.setTargetPosition(target);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(0.5);
    }


    private void slideDrop() {
        //slideMotor.setDirection(DcMotor.Direction.REVERSE);
        //slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //int target = slideMotor.getCurrentPosition() - count;
        /*
        if (target <= 0) {
            target = 0;
        }
        */
        //telemetry.addData("count", "%d", count);
        //telemetry.update();
        if (random % 2 == 1) {
            slideMotor.setDirection(DcMotor.Direction.FORWARD);
        } else {
            slideMotor.setDirection(DcMotor.Direction.REVERSE);
        }
        slideMotor.setTargetPosition(0);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(1);
        random++;
    }


    public Boolean poleDetect() {
        if (color.green() > color.red() && color.green() > color.blue()) {
            telemetry.addLine("highest is green");
            if (color.green() >= 80) {
                telemetry.addLine("Pole detected");
                return true;
            }
        } else {
            telemetry.addLine("green not highest");
        }
        return false;
    }


    private class stopThread extends Thread {
        public stopThread() {
            this.setName("stopThread");
        }

        // called when tread.start is called. thread stays in loop to do what it does until exit is
        // signaled by main code calling thread.interrupt.
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    stopThreadRunning = 1;
                    leftWheelF.setPower(0);
                    leftWheelR.setPower(0);

                    rightWheelF.setPower(0);
                    rightWheelR.setPower(0);

                    // if gamepad1.a is clicked, stop the thread and set stopThreadRunning = 0
                    if (gamepad1.a) {
                        Thread.currentThread().interrupt();
                        stopThreadRunning = 0;
                        return;

                    }
                }
            }
            // interrupted means time to shutdown. note we can stop by detecting isInterrupted = true
            // or by the interrupted exception thrown from the sleep function.
            // an error occurred in the run loop.
            catch (Exception e) {

            }

        }
    }
}
