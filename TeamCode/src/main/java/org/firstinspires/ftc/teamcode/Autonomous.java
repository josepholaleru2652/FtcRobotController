package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

// REMEMBER TO CHANGE THIS BACK AFTER TESTING SO IT SHOWS UP RIGHT ON DRIVERHUB
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

        // we switched it from while -> if so that it only runs once when u start OpMode, may cause errors but why not!
        if (opModeIsActive()) {
            moveLinearY(0.5, 24); // move forward 24 inches
            sleep(1000);
            moveLinearY(0.5, -24); // move back 24 inches
            sleep(1000);
            strafeByInches(0.5, 24);   // right
            sleep(1000);
            strafeByInches(0.5, -24);  // left
            //PLEASE REMEMBER TO MAKE INCHES NEGATIVE AND NOT POWER. THE ENCODER SETS DIRECTION BASED OFF HOW MANY POS OR NEG ENCODER TICKS IT NEEDS (24 OR -24, not the 0.5)
        }
    }
    
    public void moveLinearY(double power, double inches) {
        double TICKS_PER_REV = 537.7;
        double WHEEL_DIAMETER_INCHES = 4.09;
        double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_INCHES);
    
        // distance in ticks, direction handled by sign of power
        int targetPosition = (int) (inches * TICKS_PER_INCH);
    
        // set new target positions
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + targetPosition);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + targetPosition);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + targetPosition);
        backRight.setTargetPosition(backRight.getCurrentPosition() + targetPosition);
    
        // switch to RUN_TO_POSITION
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    
        // set power
        setAllPower(power);
    
        // wait until any motor still busy
        while (opModeIsActive() && 
               (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            telemetry.addData("Linear Move", "Target: %d | Direction: %s", targetPosition,
                    inches >= 0 ? "Forward" : "Backward");
            telemetry.update();
        }
    
        stopAllMotors();
    
        // reset mode
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    //POSITIVE INCHES MEANS STRAFE RIGHT
    public void moveLinearX(double power, double inches) {
        double TICKS_PER_REV = 537.7;
        double WHEEL_DIAMETER_INCHES = 4.09;
        double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_INCHES);
    
        // Strafing isn't 1:1 with forward motion due to mecanum angle (~45°)
        // So we apply a correction factor (~1.414)
        double STRAFE_CORRECTION = 1.414; // sqrt(2) KEEP IN MIND WE WILL HAVE TO TWEAK OR REMOVE THIS
        int targetPosition = (int) (inches * TICKS_PER_INCH * STRAFE_CORRECTION);
    
        // For right strafe (+inches)
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + targetPosition);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() - targetPosition);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() - targetPosition);
        backRight.setTargetPosition(backRight.getCurrentPosition() + targetPosition);
    
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    
        setAllPower(Math.abs(power)); // all positive, encoders handle direction
    
        while (opModeIsActive() && 
              (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            telemetry.addData("Strafing", "Target: %d | Direction: %s", targetPosition,
                    inches >= 0 ? "Right" : "Left");
            telemetry.update();
        }
    
        stopAllMotors();
    
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    /*
    public void moveForwardByInches(double power, double inches) {
        double TICKS_PER_REV = 537.7; // find ticks per full rotation for motors
        double WHEEL_DIAMETER_INCHES = 4.09; // find wheel diameter
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

        // Wait while any motor is busy (not just fronts)
        while (opModeIsActive() &&
              (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            telemetry.addData("Moving", "Target: %d", targetPosition);
            telemetry.update();
        }

        stopAllMotors();

        // Set motors back to normal mode
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    
    public void moveBackwardByInches(double power, double inches) {
        double TICKS_PER_REV = 537.7; // find ticks per full rotation for motors
        double WHEEL_DIAMETER_INCHES = 4.09; // find wheel diameter
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

        setAllPower(-power, -power, -power, -power);

        // Wait while any motor is busy (not just fronts)
        while (opModeIsActive() &&
              (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            telemetry.addData("Moving", "Target: %d", targetPosition);
            telemetry.update();
        }

        stopAllMotors();

        // Set motors back to normal mode
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    */
    private void setAllPower(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }
    public void stopAllMotors() {
        setAllPower(0); //only one argument now instead of 4
    }
    /*
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
    */
}
