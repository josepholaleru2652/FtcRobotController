package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.ArrayList;

@TeleOp(name = "Outtake", group = "OpMode")
public class Outtake extends OpMode {

    private DcMotor outtakeMotor;
    private double i = 0.01;

     AprilTagProcessor tagProcessor;
     VisionPortal visionPortal;

    @Override
    public void init() {
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        tagProcessor = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Limelight 1"))
                .addProcessor(tagProcessor)
                .build();

        telemetry.addData("Status", "Motors init success");
        telemetry.update();
    }

    @Override
    public void loop() {

        ArrayList<AprilTagDetection> detections = tagProcessor.getDetections();

        boolean tagDetected = (detections.size() > 0);


        // Gradually ramp up to max speed
        if (i < 1) {
            i += 0.01;
        }
        i = Range.clip(i, 0, 1);

        outtakeMotor.setPower(i);
        telemetry.addData("Outtake Power", i);
        telemetry.update();
    }

    @Override
    public void stop() {
        outtakeMotor.setPower(0);
    }
}
