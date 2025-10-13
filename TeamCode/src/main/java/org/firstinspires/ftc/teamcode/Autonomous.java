package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="DriveTrain_Linear", group="TeleOp")
public class Autonomous extends LinearOpMode {
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        // Initialize motors
        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        // Reverse the right side motors
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        // reset encoders and set motors to run using encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for driver to press PLAY
        waitForStart();

        while (opModeIsActive()) {
            moveForwardByInches(0.5, 24); // move forward 24 inches
            sleep(1000); // this code runs it using time instead of encode
            moveForwardByInches(0.5, -24); // move back 24 inches
        }
    }
    public void moveForwardByInches(double power, double inches) {
        int TICKS_PER_REV = 6767; // find ticks per full rotation for motors
        double WHEEL_DIAMETER_INCHES = 67.41; // find wheel diameter
        double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_INCHES); // some math stuff ig

        int targetPosition = (int) (inches * TICKS_PER_INCH);

        // Set target positions for all motors
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + targetPosition);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + targetPosition);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + targetPosition);
        backRight.setTargetPosition(backRight.getCurrentPosition() + targetPosition);

        // Switch to RUN_TO_POSITION mode
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setAllPower(power, power, power, power);

        // Wait until motors reach target
        while (opModeIsActive() && frontLeft.isBusy() && frontRight.isBusy()) {
            telemetry.addData("Moving Forward", "Target: %d", targetPosition);
            telemetry.update();
        }

        stopAllMotors();

        // Set motors back to normal mode
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    private void setAllPower(double fl, double fr, double bl, double br) {
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }
    public void stopAllMotors() {
        setAllPower(0, 0, 0, 0);
    }
    public void moveForward(double power, long time) {
        setAllPower(-power, -power, -power, -power);
        sleep(time);
        stopAllMotors();
    }
    public void moveBackwards(double power, long time) {
        setAllPower(power, power, power, power);
        sleep(time);
        stopAllMotors();
    }
    public void strafeRight(double power, long time) {
        setAllPower(power, -power, -power, power);
        sleep(time);
        stopAllMotors();
    }
    public void strafeLeft(double power, long time) {
        setAllPower(-power, power, power, -power);
        sleep(time);
        stopAllMotors();
    }
    public void turnRight(double power, long time) {
        setAllPower(-power, power, -power, power);
        sleep(time);
        stopAllMotors();
    }
    public void turnLeft(double power, long time) {
        setAllPower(power, -power, power, -power);
        sleep(time);
        stopAllMotors();
    }
}
