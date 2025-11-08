package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import android.graphics.Color;
import java.util.Arrays;

import org.w3c.dom.ranges.Range;

@TeleOp(name = "FullTeleOp_R2ManualLaunch", group = "OpMode")
public class TeleOp_R2ManualLaunch extends OpMode {

    private Servo storageServo;
    private Servo doorServo;
    private ColorSensor colorSensor;
    private DcMotor intakeMotor;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor outtakeMotor;
    
    //use if colorsensor
    //private String[] storageColorOrder = new String[3];
    //private int filledSlots = 0;
    //private boolean filling = true;
    //private boolean isLaunching = false;

    private double kickerPosition = 0.0;
    private double outtakePower = 0.0;
    private double power = 0.01;
    private double[] servoPos = {0.0, 0.33, 0.66};
    private int i = 0;
    boolean changed = false;
    boolean powerChanged = false;
    boolean powerIncrease = true;


    @Override
    public void init(){
        storageServo = hardwareMap.get(Servo.class, "storageServo");
        doorServo = hardwareMap.get(Servo.class, "doorServo");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");


        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        storageServo.setPosition(servoPos[i]);
        doorServo.setPosition(kickerPosition);
        
        telemetry.addData("Status", "everything initialized");
        telemetry.update();
    }
    public void loop(){

        //drive train code

        double drive = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;
        double strafe = gamepad1.left_stick_x;

        double frontLeftPower = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower = drive - strafe + turn;
        double backRightPower = drive + strafe - turn;

        double max = Math.max(Math.abs(frontLeftPower),
            Math.max(Math.abs(frontRightPower),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);


            if(gamepad1.right_trigger>0.5){
                kickerPosition += 0.25;
                if (kickerPosition > 0.25){
                    kickerPosition = 0.25;
                }
            }

            if(gamepad1.right_trigger<= 0.5){
                kickerPosition = 0.0;
                if (kickerPosition<0.0){
                    kickerPosition = 0.0;
                }
            }
            if (gamepad1.right_trigger > 0.5 && !changed) {
                if (kickerPosition == 0.0) {
                    kickerPosition = 0.25;
                } else {
                    kickerPosition = 0.0;
                }
                changed = true;
                } else if (gamepad1.right_trigger <= 0.5) {
                    changed = false;
                }

                doorServo.setPosition(kickerPosition);

                if (gamepad1.right_bumper > 0.5 && !powerChanged) {
                    if (powerIncrease) {
                        power += 0.1;
                    if (power > 1.0) power = 1.0;
                    } else {
                        power -= 0.1;
                        if (power < 0.0) power = 0.0;
                    }
                    powerIncrease = !powerIncrease;
                    powerChanged = true;
                    } else if (gamepad1.right_bumper <= 0.5) {
                        powerChanged = false;
                    }

outtakeMotor.setPower(power);
            power = Range.clip(power, 0.0, 1.0);
            outtakeMotor.setPower(power);


            if (i<2 && gamepad.a > 0.5){
                i++;
            }
            if (i == 3){
                telemetry.addData("Important", "please rotate back to position 0.0 (storage servo)");
                telemetry.update();
            }
            if (i>0 && gamepad.b > 0.5){
                i--;
            }
            
        //mention if robot is strafing, turning, or going forward/backwards
        telemetry.addData("Status", "Running");
        if (Math.abs(strafe) > 0.1) {
            telemetry.addData("Movement", "Strafing");
        } else if (Math.abs(turn) > 0.1) {
            telemetry.addData("Movement", "Turning");
        } else if (Math.abs(drive) > 0.1) {
            telemetry.addData("Movement", "Driving");
        } else {
            telemetry.addData("Movement", "Idle");
        }
        telemetry.addData("frontLeftPower", frontLeftPower);
        //mention if negative or positive
        if (frontLeftPower > 0) {
            telemetry.addData("frontLeftDirection", "Backward");
        } else if (frontLeftPower < 0) {
            telemetry.addData("frontLeftDirection", "Forward");
        } else {
            telemetry.addData("frontLeftDirection", "Stopped");
        }
        telemetry.addData("frontRightPower", frontRightPower);
        //mention if negative or positive
        if (frontRightPower > 0) {
            telemetry.addData("frontRightDirection", "Backward");
        } else if (frontRightPower < 0) {
            telemetry.addData("frontRightDirection", "Forward");
        } else {
            telemetry.addData("frontRightDirection", "Stopped");
        }
        telemetry.addData("backLeftPower", backLeftPower);
        //mention if negative or positive
        if (backLeftPower > 0) {
            telemetry.addData("backLeftDirection", "Backward");
        } else if (backLeftPower < 0) {
            telemetry.addData("backLeftDirection", "Forward");
        } else {
            telemetry.addData("backLeftDirection", "Stopped");
        }
        telemetry.addData("backRightPower", backRightPower);
        if (backRightPower > 0) {
            telemetry.addData("backRightDirection", "Backward");
        } else if (backRightPower < 0) {
            telemetry.addData("backRightDirection", "Forward");
        } else {
            telemetry.addData("backRightDirection", "Stopped");
        }
        telemetry.update();
    }
}
