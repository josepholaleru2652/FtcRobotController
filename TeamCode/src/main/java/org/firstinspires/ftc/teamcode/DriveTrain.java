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
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

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

        double drive = -gamepad1.left_stick_y; // forward/backward
        double turn = gamepad1.right_stick_x;  // rotation
        double strafe = gamepad1.left_stick_x; // lateral

        double frontLeftPower  = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower   = drive - strafe + turn;
        double backRightPower  = drive + strafe - turn;

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        telemetry.addData("frontLeftPower", frontLeftPower);
        telemetry.addData("frontRightPower", frontRightPower);
        telemetry.addData("backLeftPower", backLeftPower);
        telemetry.addData("backRightPower", backRightPower);
        telemetry.update();
    }
}



