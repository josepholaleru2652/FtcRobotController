package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

@TeleOp(name="Intake", group="OpMode")
public class Intake extends OpMode {
    private DcMotor intakeMotor;
    private DistanceSensor distanceSensor;

    @Override
    public void init() {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

        telemetry.addData("Status", "Intake Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (distanceSensor.getDistance(DistanceSensor.DistanceUnit.CM) < 5.0) { //5cm
            intakeMotor.setPower(1.0); //run intake motor
        } else {
            intakeMotor.setPower(0.0); //stop intake motor
        }
        //if r2 pressed run intake
        if(gamepad1.right_trigger > 0) {
            intakeMotor.setPower(1.0);
        } else {
            intakeMotor.setPower(0.0);
        }
    }
}
