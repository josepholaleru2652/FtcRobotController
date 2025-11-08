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
// All Vision/AprilTag imports have been removed

@TeleOp(name = "FullTeleOp_R2ManualLaunch", group = "OpMode")
public class TeleOp_R2ManualLaunch extends OpMode {

    // --- Hardware Declarations ---
    private Servo storageServo;
    private Servo doorServo;
    private ColorSensor colorSensor;
    private DcMotor intakeMotor;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor outtakeMotor;

    // --- Storage & State Logic ---
    private String[] storageColorOrder = new String[3]; // What's in the drum
    private String[] limelightColorOrder = new String[3]; // What we *want* to launch
    private int filledSlots = 0;
    private int launchIndex = 0;
    private boolean filling = true;
    private boolean isLaunching = false; // True when kicker servo is moving
    private boolean isRamping = false;   // True when outtake is spinning up

    private double outtakePower = 0.0;
    private double rampSpeed = 0.1; // Speed to ramp up outtake
    private ElapsedTime timer = new ElapsedTime();
    private double[] servoPositions = {0.0, 0.33, 0.66};
    private double flickStartTime = 0;

    // --- Manual Color Input System ---
    private int inputSlot = 0; // Tracks which slot (0, 1, or 2) we are setting
    private boolean isOrderSet = false; // Becomes true when an order is selected
    // Edge detection for button presses
    private boolean a_now = false, a_prev = false;
    private boolean b_now = false, b_prev = false;
    private boolean y_now = false, y_prev = false; // Added Y button

    // Define preset launch orders
    private final String[] ORDER_A = {"Green", "Purple", "Purple"}; // Example order 1
    private final String[] ORDER_B = {"Purple", "Green", "Purple"}; // Example order 2
    private final String[] ORDER_Y = {"Purple", "Purple", "Green"}; // Example order 3

    @Override
    public void init() {
        // Map all hardware
        storageServo = hardwareMap.get(Servo.class, "storageServo");
        doorServo = hardwareMap.get(Servo.class, "doorServo");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");

        // Set motor directions
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        // Assuming intake runs forward
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        // Set initial servo positions
        storageServo.setPosition(servoPositions[0]);
        doorServo.setPosition(0.0);

        // --- Initialize Manual Input System ---
        limelightColorOrder = new String[3]; // All slots are null
        isOrderSet = false;

        telemetry.addData("Status", "All Systems Initialized");
        telemetry.addData(">", "Ready to select color order...");
        telemetry.addData(">", "Press A, B, or Y on Gamepad 1.");
        telemetry.update();
    }

    /**
     * This method runs repeatedly on the Driver Hub
     * AFTER "Init" is pressed, but BEFORE "Start" is pressed.
     */
    @Override
    public void init_loop() {
        // Update current button states
        a_now = gamepad1.a;
        b_now = gamepad1.b;
        y_now = gamepad1.y;

        // --- Handle Input ---
        if (!isOrderSet) {
            // Select Order A: Green, Purple, Purple
            if (a_now && !a_prev) {
                limelightColorOrder = ORDER_A;
                isOrderSet = true;
            }
            // Select Order B: Purple, Green, Purple
            if (b_now && !b_prev) {
                limelightColorOrder = ORDER_B;
                isOrderSet = true;
            }
            // Select Order Y: Purple, Purple, Green
            if (y_now && !y_prev) {
                limelightColorOrder = ORDER_Y;
                isOrderSet = true;
            }
        }

        // --- Update Telemetry ---
        telemetry.addData("--- MANUAL ORDER SELECTION ---", "");
        telemetry.addData("A", "G, P, P (Order A)");
        telemetry.addData("B", "P, G, P (Order B)");
        telemetry.addData("Y", "P, P, G (Order Y)");
        telemetry.addData("Launch Order", formatColorOrder(limelightColorOrder));

        // Check if order is complete
        if (isOrderSet) {
            telemetry.addData("STATUS", "LOCKED! Order Selected: " + formatColorOrder(limelightColorOrder));
        } else {
            telemetry.addData("STATUS", "WAITING FOR SELECTION...");
        }

        // Update previous button states for edge detection
        a_prev = a_now;
        b_prev = b_now;
        y_prev = y_now;

        telemetry.update();
    }

    @Override
    public void loop() {
        // Only run the robot logic if the order was successfully set in init_loop
        if (isOrderSet) {
            // --- Mecanum Drive Code ---
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

            // --- Storage and Launching FSM ---

            if (filling) {
                handleFillingPhase();
                intakeMotor.setPower(1.0); // Keep intake on during filling
            }

            // --- LAUNCHING LOGIC (Manual R2) ---
            if (!filling && !isLaunching && !isRamping) {
                // Robot is in LAUNCH mode, waiting for driver.
                intakeMotor.setPower(0); // Turn intake off during launch

                // 1. Find the correct color to launch next
                String targetColor = limelightColorOrder[launchIndex];

                // 2. Find where that color is in our storage
                int colorSlot = findColorIndex(storageColorOrder, targetColor);

                // 3. Rotate storage to that position *before* launching
                rotateToPosition(colorSlot);

                // 4. Now, wait for the driver to press R2
                telemetry.addData("Launcher", "Ball ready: " + (targetColor != null ? targetColor : "N/A"));
                telemetry.addData(">", "Press R2 (Right Trigger) to LAUNCH!");

                if (gamepad1.right_trigger > 0.5) {
                    isRamping = true;
                    timer.reset();
                } else {
                    // While waiting for R2, keep outtake at idle
                    outtakeMotor.setPower(0.3);
                    outtakePower = 0.3;
                }
            }

            // --- Outtake Ramping Process ---
            if (isRamping) {
                if (outtakePower < 1.0) {
                    outtakePower += rampSpeed; // Ramp up
                    outtakeMotor.setPower(outtakePower);
                    telemetry.addData("Outtake", "Ramping up...");
                } else {
                    // Fully ramped, ready to fire
                    outtakePower = 1.0;
                    outtakeMotor.setPower(outtakePower);
                    isRamping = false;     // Stop ramping
                    isLaunching = true;    // Start launching
                    flickStartTime = timer.milliseconds();
                    doorServo.setPosition(1.0); // Fire kicker
                }
            }

            // --- Firing Kicker Process ---
            if (isLaunching && (timer.milliseconds() - flickStartTime > 250)) {
                doorServo.setPosition(0.0); // Retract kicker
                outtakeMotor.setPower(0.3); // Set outtake back to idle
                outtakePower = 0.3;
                isLaunching = false;
                launchIndex++; // Move to the next target color

                if (launchIndex >= 3) {
                    // Finished launching all 3, reset
                    filledSlots = 0;
                    storageColorOrder = new String[3];
                    filling = true; // Go back to filling mode
                    launchIndex = 0;
                } else {
                    sleep(500); // Wait half a second before finding next ball
                }
            }

            // --- Telemetry ---
            outtakeMotor.setPower(outtakePower); // Ensure motor power is always set
            telemetry.addData("Outtake Power", outtakePower);
            telemetry.addData("Mode", filling ? "Filling" : "Launching");
            telemetry.addData("Stored", formatColorOrder(storageColorOrder));
            telemetry.addData("Target", formatColorOrder(limelightColorOrder));
            telemetry.addData("Next Target", (launchIndex < 3 && limelightColorOrder[launchIndex] != null) ? limelightColorOrder[launchIndex] : "Done");
            telemetry.addData("Filled Slots", filledSlots);
            telemetry.update();

        } else {
            // This runs if "Start" was pressed before the order was set
            telemetry.addData("ERROR", "Color order not set!");
            telemetry.addData(">", "Please restart the OpMode and set the order.");
            telemetry.update();
        }
    }

    // A helper function to make the array look nice on telemetry
    private String formatColorOrder(String[] order) {
        String[] displayOrder = new String[3];
        for (int i = 0; i < 3; i++) {
            if (order[i] == null) {
                displayOrder[i] = "-"; // Empty slot
            } else if (order[i].equals("Purple")) {
                displayOrder[i] = "P";
            } else if (order[i].equals("Green")) {
                displayOrder[i] = "G";
            } else {
                displayOrder[i] = "?";
            }
        }
        return Arrays.toString(displayOrder); // e.g., "[P, G, -]"
    }

    // ====== FILLING PHASE ======
    private void handleFillingPhase() {
        String color = detectColor();
        if (!color.equals("Unknown") && filledSlots < 3) {
            storageColorOrder[filledSlots] = color;
            filledSlots++;
            if (filledSlots < 3) {
                rotateToPosition(filledSlots);
            } else {
                // We are now full, switch to launching mode
                filling = false;
                launchIndex = 0;
            }
            sleep(800); // Wait for ball to settle
        }
    }

    // ====== HELPER FUNCTIONS ======

    private void rotateToPosition(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            storageServo.setPosition(servoPositions[slotIndex]);
        }
    }

    private String detectColor() {
        int red = colorSensor.red();
        int green = colorSensor.green();
        int blue = colorSensor.blue();

        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        float hue = hsv[0];

        // Loosened Purple range slightly
        if (hue >= 200 && hue <= 260) {
            return "Purple";
        } else if (hue >= 125 && hue <= 180) {
            return "Green";
        } else {
            return "Unknown";
        }
    }

    private int findColorIndex(String[] arr, String color) {
        // Added a check for a null target color
        if (color == null) {
            return 0; // Don't move if target is null
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].equals(color)) {
                return i;
            }
        }
        return 0; // Default to slot 0 if not found
    }

    private void sleep(long ms) {
        double start = timer.milliseconds();
        while (timer.milliseconds() - start < ms && opModeIsActive()) {
            // idle
        }
    }

    // All Limelight/AprilTag functions have been removed.
}
