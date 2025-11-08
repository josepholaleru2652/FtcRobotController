package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

// Tutorial: https://docs.google.com/document/d/1hLlhlEqIOicfobUs0HxBQp0qJFio-bUea9mtKIdgB2U/edit?tab=t.0

@Autonomous(name = "OTOS Full Auto (Stable)")
public class OTOSFullAuto extends LinearOpMode {

    // --- Hardware ---
    private SparkFunOTOS myOtos;
    private DcMotor leftFront, leftBack, rightFront, rightBack;
    private DcMotor intakeMotor, outtakeMotor;
    private Servo storageServo;

    // --- Config ---
    private double power = 0.01;
    private final double[] servoPositions = {0.0, 0.33, 0.66};
    private final double POSITION_TOLERANCE = 0.5; // inches
    private final double HEADING_TOLERANCE = 1;    // degrees

    @Override
    public void runOpMode() throws InterruptedException {
        // ======================================
        // === INIT & Configure OTOS (Step 3) === 
        // ======================================
        //initHardware();
        //configureOtos();

        telemetry.addLine("Initialization complete. Ready to start.");
        telemetry.update();

        waitForStart();
        if (!opModeIsActive()) return;

        // ====================================
        // =====   TEST BLOCKS (Step 4)  ======
        // ====================================

        // ---- OTOS Position Test (A) ----
        // Uncomment to move robot forward 12" check telemetry, and write down change in y (3 times if possible)
        // driveTo(new SparkFunOTOS.Pose2D(0, 12, 0), 0.3);

        // ---- OTOS Strafe Test (B) ----
        // Uncomment to strafe 12" right check telemetry, and write down change in x (3 times if possible)
        // driveTo(new SparkFunOTOS.Pose2D(12, 0, 0), 0.3);

        // ---- OTOS Rotate Test (C) ----
        // Uncomment to rotate 90 degrees and check heading (3 times if possible)
        // rotateTo(90, 0.3);

        // ---- Intake Test (D) ----
        // Uncomment to run intake for 2 seconds (Test 3 balls)
        // intakeMotor.setPower(1);
        // sleep(2000);
        // intakeMotor.setPower(0);

        // ---- Outtake Test (E) ----
        // Uncomment to ramp outtake power to 1 and back down (Test 3 shots from shooting point)
        // rampOuttake();

        // ---- Servo Test (F) ----
        // Uncomment to cycle through servo positions
        // for (double pos : servoPositions) {
        //     storageServo.setPosition(pos);
        //     sleep(500);
        // }

        /*
        =================================================================
        = UNCOMMENT TO RUN FULL POSITION 1 AUTONOMOUS SEQUENCE (Step 5) =
        =================================================================

        // === AUTONOMOUS SEQUENCE (POS #1) ===
        // Move forward 28 inches
        driveTo(new SparkFunOTOS.Pose2D(0, 28, 0), 0.5);

        // Turn left 90°
        rotateTo(-90, 0.3);

        // Start intake while moving to pickup
        intakeMotor.setPower(1);
        driveTo(new SparkFunOTOS.Pose2D(-39, 28, -90), 0.5);

        // Move back to drop position
        driveTo(new SparkFunOTOS.Pose2D(16, 28, -90), 0.5);
        intakeMotor.setPower(0);

        // Face forward again
        rotateTo(0, 0.3);

        // Drive to backdrop
        driveTo(new SparkFunOTOS.Pose2D(0, 75, 0), 0.5);

        // Turn slightly to align for scoring
        rotateTo(-45, 0.3);

        // Outtake sequence
        rampOuttakeAndScore();

        setDrivetrainPower(0, 0, 0);
        telemetry.addLine("AUTO COMPLETE.");
        telemetry.update();
        sleep(1000);

         */
    }

    // --- Initialization ---
    private void initHardware() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        storageServo = hardwareMap.get(Servo.class, "storageServo");
        storageServo.setPosition(servoPositions[0]);

        myOtos = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");
    }

    private void configureOtos() {
        telemetry.addLine("Configuring OTOS...");
        telemetry.update();

        myOtos.setLinearUnit(DistanceUnit.INCH);
        myOtos.setAngularUnit(AngleUnit.DEGREES);
        myOtos.setOffset(new SparkFunOTOS.Pose2D(0, 0, 0));
        myOtos.setLinearScalar(1.0);
        myOtos.setAngularScalar(1.0);
        myOtos.calibrateImu();
        myOtos.resetTracking();
        myOtos.setPosition(new SparkFunOTOS.Pose2D(0, 0, 0));

        telemetry.addLine("OTOS configured!");
        telemetry.update();
    }

    // --- Motion Control ---
    private void driveTo(SparkFunOTOS.Pose2D target, double maxPower) {
        while (opModeIsActive() && distance(myOtos.getPosition(), target) > POSITION_TOLERANCE) {
            SparkFunOTOS.Pose2D current = myOtos.getPosition();

            double errorX = target.x - current.x;
            double errorY = target.y - current.y;

            double powerX = clamp(errorX * 0.05, -maxPower, maxPower);
            double powerY = clamp(errorY * 0.05, -maxPower, maxPower);

            setDrivetrainPower(powerX, powerY, 0);

            telemetry.addData("DriveTo Target", "(%.1f, %.1f)", target.x, target.y);
            telemetry.addData("Current", "(%.1f, %.1f)", current.x, current.y);
            telemetry.update();

            sleep(25); // prevents OTOS data freeze
        }
        setDrivetrainPower(0, 0, 0);
    }

    private void rotateTo(double targetHeading, double maxPower) {
        while (opModeIsActive() && Math.abs(angleDiff(myOtos.getPosition().h, targetHeading)) > HEADING_TOLERANCE) {
            double error = angleDiff(myOtos.getPosition().h, targetHeading);
            double power = clamp(error * 0.01, -maxPower, maxPower);

            setDrivetrainPower(0, 0, power);

            telemetry.addData("Rotate Target", "%.1f°", targetHeading);
            telemetry.addData("Current", "%.1f°", myOtos.getPosition().h);
            telemetry.update();

            sleep(25);
        }
        setDrivetrainPower(0, 0, 0);
    }

    private void rampOuttake() {
        while(opModeIsActive() && power < 1.0) {
            power += 0.01;
            power = Range.clip(power, 0, 1);
            outtakeMotor.setPower(power);
            telemetry.addData("Outtake Power", "%.2f", power);
            telemetry.update();
            sleep(20);
        }
        outtakeMotor.setPower(0);
    }

    private void rampOuttakeAndScore() {
        while (opModeIsActive() && power < 1.0) {
            power += 0.01;
            power = Range.clip(power, 0.0, 1.0);
            outtakeMotor.setPower(power);

            telemetry.addData("Outtake Power", "%.2f", power);
            telemetry.update();
            sleep(20);
        }

        storageServo.setPosition(servoPositions[2]); // drop item
        sleep(500);
        storageServo.setPosition(servoPositions[0]); // reset servo
    }

    // --- Utilities ---
    private double distance(SparkFunOTOS.Pose2D a, SparkFunOTOS.Pose2D b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.hypot(dx, dy);
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    private double angleDiff(double current, double target) {
        double diff = target - current;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        return diff;
    }

    private void setDrivetrainPower(double x, double y, double rot) {
        leftFront.setPower(y + x + rot);
        rightFront.setPower(y - x - rot);
        leftBack.setPower(y - x + rot);
        rightBack.setPower(y + x - rot);
    }
}
