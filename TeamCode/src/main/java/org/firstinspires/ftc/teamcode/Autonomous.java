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

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for driver to press PLAY
        waitForStart();

        while (opModeIsActive()) {

        }
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
        setAllPower(power, power, power, power);
        sleep(time);
        stopAllMotors();
    }
    public void moveBackwards(double power, long time) {
        setAllPower(-power, -power, -power, -power);
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
        setAllPower(power, -power, power, -power);
        sleep(time);
        stopAllMotors();
    }
    public void turnLeft(double power, long time) {
        setAllPower(-power, power, -power, power);
        sleep(time);
        stopAllMotors();
    }
}
