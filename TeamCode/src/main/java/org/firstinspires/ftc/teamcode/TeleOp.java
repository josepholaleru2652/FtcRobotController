package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "TeleOp", group = "OpMode")
public class TeleOp extends OpMode {

    private Servo storageServo;
    private Servo doorServo;
    private DcMotor intakeMotor;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor outtakeMotor;

    private double power = 0.0; // ramping outtake power
    private int powerToggle = -1; // -1 = off, 1 = on
    private boolean lastBumperState = false;
    private boolean lastLeftBumper = false;
    private boolean intakeRunning = false;

    private double kickerPosition = 0.0;
    private boolean buttonHeld = false;

    private double[] servoPos = {0.0, 0.33, 0.66};
    private int i = 0;

    @Override
    public void init() {
        storageServo = hardwareMap.get(Servo.class, "storageServo");
        doorServo = hardwareMap.get(Servo.class, "doorServo");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        storageServo.setPosition(servoPos[i]);
        doorServo.setPosition(kickerPosition);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        // --- Drivetrain ---
        double drive = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;
        double strafe = gamepad1.left_stick_x;

        double fl = drive + strafe + turn;
        double fr = drive - strafe - turn;
        double bl = drive - strafe + turn;
        double br = drive + strafe - turn;

        double max = Math.max(Math.abs(fl), Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br))));
        if (max > 1.0) {
            fl /= max; fr /= max; bl /= max; br /= max;
        }

        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);

        // --- Intake Toggle ---
        boolean currentLeftBumper = gamepad1.left_bumper;
        if (currentLeftBumper && !lastLeftBumper) {
            intakeRunning = !intakeRunning;
            intakeMotor.setPower(intakeRunning ? 1.0 : 0.0);
        }
        lastLeftBumper = currentLeftBumper;

        // --- Kick-Up Lever Servo ---
        if (gamepad1.right_trigger > 0.5) {
            kickerPosition = 0.25;
            buttonHeld = true;
        } else if (buttonHeld) {
            kickerPosition = 0.0;
            buttonHeld = false;
        }
        doorServo.setPosition(kickerPosition);

        // --- Outtake Motor Toggle with Ramp ---
        boolean currentBumperState = gamepad1.right_bumper;
        if (currentBumperState && !lastBumperState) {
            powerToggle *= -1; // flip on/off
            if (powerToggle == -1) {
                power = 0.0; // reset if turning off
                outtakeMotor.setPower(0.0);
            }
        }
        lastBumperState = currentBumperState;

        if (powerToggle == 1) {
            if (power < 1.0) {
                power += 0.01; // ramp speed
                power = Range.clip(power, 0.0, 1.0);
            }
            outtakeMotor.setPower(power);
        } else {
            power = 0.0;
            outtakeMotor.setPower(0.0);
        }

        // --- Storage Servo ---
        if (i < 2 && gamepad1.a) i++;
        if (i > 0 && gamepad1.b) i--;
        storageServo.setPosition(servoPos[i]);

        // --- Telemetry ---
        telemetry.addData("Drive", "FL: %.2f FR: %.2f BL: %.2f BR: %.2f", fl, fr, bl, br);
        telemetry.addData("Intake", intakeRunning ? "On" : "Off");
        telemetry.addData("Outtake Power", String.format("%.2f", power));
        telemetry.addData("Storage Servo", "%.2f", servoPos[i]);
        telemetry.addData("Kick Servo", "%.2f", kickerPosition);
        telemetry.update();
    }
}
