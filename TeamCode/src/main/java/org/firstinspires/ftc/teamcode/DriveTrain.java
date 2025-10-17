//creating a working code for the drive train of out FTC Robot
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime; //so we can check time if we feel like iy
import com.qualcomm.robotcore.util.Range; //we used the range method just to keep the motor powers to 1 and -1
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode; //importing LinearOp Mode libraries, which is for autonomous driving
import com.qualcomm.robotcore.eventloop.opmode.OpMode; //this is the iterative(loop) version of Linear so yeah
import com.qualcomm.robotcore.eventloop.opmode.TeleOp; // importing TeleOp Mode libraries, for robot controlled by driving
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="DriveTrain", group="OpMode")
public class DriveTrain extends OpMode {

    //making 4 dc motors for the drive train work
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;


    //we cant use runOpMode() method here
    //its TeleOp so we're using the reg OpMode instead of Linear Op
    //instead we gta use iterative methods instead like init() and loop()

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");//0
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");//1
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");//2
        backRight = hardwareMap.get(DcMotor.class, "backRight");//3

        //making motors either move backwords, frontwards,or left/right
        //the reason the right ones are reversed it because they're facing an opposite direction from the left
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Motors init success");
        telemetry.update();

        //i dont see a specific problem with this its just it's not needed for Tele
        //what this does is it that doesnt run anything else until you start the code
        //although maybe it could be a problem if the entire function runs and we have to hit start every iteration
        // waitForStart()
    }


    public void loop() {
        // Forward/backward: -left_stick_y
        // Turn left/right: left_stick_x

        double drive = gamepad1.left_stick_y; // forward/backward
        double turn = -gamepad1.right_stick_x;  // rotation
        double strafe = gamepad1.left_stick_x; // lateral

        double frontLeftPower  = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower   = drive - strafe + turn;
        double backRightPower  = drive + strafe - turn;

        double max = Math.max(Math.abs(frontLeftPower),
             Math.max(Math.abs(frontRightPower),
             Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        if (max > 0.8) {
            frontLeftPower  /= max;
            frontRightPower /= max;
            backLeftPower   /= max;
            backRightPower  /= max;
        }
        
        

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
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
