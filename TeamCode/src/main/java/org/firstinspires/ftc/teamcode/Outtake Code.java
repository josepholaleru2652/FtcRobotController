package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Outtake Diagnostic", group = "OpMode")
public class Outtake extends OpMode {

    private DcMotor outtakeMotor;
    private double i = 0.01;

    // Diagnostic variables
    private ElapsedTime runtime = new ElapsedTime();
    private double lastTime = 0;
    private double lastPosition = 0;
    private double velocityTicksPerSec = 0;
    private double rpm = 0;

    // goBILDA 5202-0002-0001 (6000 RPM, 1:1 Ratio)
    // Encoder: 28 pulses per revolution (quadrature → 112 ticks per revolution)
    private final double ticksPerRev = 112.0;

    @Override
    public void init() {
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        outtakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outtakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Motors init success");
        telemetry.update();
    }

    @Override
    public void start() {
        runtime.reset();
        lastTime = runtime.milliseconds();
        lastPosition = outtakeMotor.getCurrentPosition();
    }

    @Override
    public void loop() {
        // Gradually ramp up motor power
        if (i < 1) {
            i += 0.0001;
        }
        i = Range.clip(i, 0, 1);
        outtakeMotor.setPower(i);

        // --- Velocity & RPM Calculation ---
        double currentTime = runtime.milliseconds();
        double currentPosition = outtakeMotor.getCurrentPosition();
        double deltaTime = (currentTime - lastTime) / 1000.0; // ms → sec

        if (deltaTime > 0) {
            velocityTicksPerSec = (currentPosition - lastPosition) / deltaTime;
            rpm = (velocityTicksPerSec / ticksPerRev) * 60.0;
        }

        lastTime = currentTime;
        lastPosition = currentPosition;

        // --- Telemetry Output ---
        telemetry.addData("t_ms", (int) currentTime);
        telemetry.addData("cmdPower", "%.3f", i);
        telemetry.addData("encoderPosition", currentPosition);
        telemetry.addData("velocityTicksPerSec", "%.1f", velocityTicksPerSec);
        telemetry.addData("motorRPM", "%.1f", rpm);
        telemetry.update();
    }

    @Override
    public void stop() {
        outtakeMotor.setPower(0);
    }
}
