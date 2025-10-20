//making intake code when pressing r2 on the controller the intake spins
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Intake", group="Opmode")
public class Intake extends OpMode {
    private DcMotor intakeMotor;

    @Override
    public void init() {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        telemetry.addData("Status", "Intake Motor initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (gamepad1.right_trigger > 0) {
            intakeMotor.setPower(1);
            telemetry.addData("Status", "Intake running on power 1");
            telemetry.addData("Intake Power:", intakeMotor.getPower());
            telemetry.update();
        } else {
            intakeMotor.setPower(0);
            telemetry.addData("Status", "Intake is off");
            telemetry.addData("Intake Power:", intakeMotor.getPower());
            telemetry.update();
        }
    }
}
