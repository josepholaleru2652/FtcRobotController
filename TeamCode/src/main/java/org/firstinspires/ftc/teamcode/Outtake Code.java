package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name = "Outtake", group = "OpMode")
public class Outtake extends OpMode {

    private DcMotor outtakeMotor;
    private double power = 0.01; // initial power ramp start

    @Override
    public void init() {
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Motors init success");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Gradually ramp up power
        if (power < 1.0) {
            power += 0.01; // increment per loop (adjust as needed)
        }
        power = Range.clip(power, 0.0, 1.0);

        outtakeMotor.setPower(power);

        telemetry.addData("Outtake Power", String.format("%.2f", power));
        telemetry.update();
    }
    }

   
