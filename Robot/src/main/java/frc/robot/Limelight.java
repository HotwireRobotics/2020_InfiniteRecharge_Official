package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.DriveTrain;

public class Limelight {

    float inverted = 1;

    public float turnBuffer;

    // max is when the robot is far from the goal, min is when we're near the goal
    public float maxSpeed;
    public float minSpeed;

    public Limelight() {

    }

    public void AutoSettings() {
        turnBuffer = 0.3f;
        maxSpeed = 0.2f;
        minSpeed = 0.15f;
    }

    public void TeleopSettings() {
        turnBuffer = 1.3f;
        maxSpeed = 0.5f;
        minSpeed = 0.25f;
    }

    public void SetLight(boolean turnOn) {
        if (turnOn) {
            NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
        } else {
            NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
        }
    }

    public boolean Position(DriveTrain driveTrain, float inverted, double xAdjust) {

        SetLight(true);

        // Flip xadjust for easier tuning. Set xadjust negative to aim rigt, positive to
        // aim left.
        xAdjust = xAdjust * -1;

        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry tx = table.getEntry("tx");
        NetworkTableEntry ty = table.getEntry("ty");
        NetworkTableEntry ta = table.getEntry("ta");
        NetworkTableEntry tv = table.getEntry("tv");

        // Make sure we have valid targets first
        if (tv.getDouble(0.0f) > 0) {

            double x = tx.getDouble(0.0) + xAdjust;
            double y = ty.getDouble(0.0);
            double area = ta.getDouble(0.0);

            NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(0);

            float maxArea = 3.4f;
            float minArea = 0.26f;
            float currentAreaPercentage = ((float) area - minArea) / (maxArea - minArea);

            float currentSpeed = Lerp(minSpeed, maxSpeed, currentAreaPercentage);

            float turnSpeedSlow = -currentSpeed * 0.25f;

            if (x * inverted > turnBuffer) {

                driveTrain.SetLeftSpeed(currentSpeed * inverted);
                driveTrain.SetRightSpeed(turnSpeedSlow * inverted);

            } else if (x * inverted < -turnBuffer) {

                driveTrain.SetLeftSpeed(turnSpeedSlow * inverted);
                driveTrain.SetRightSpeed(currentSpeed * inverted);

            } else {

                driveTrain.SetLeftSpeed(0.0f);
                driveTrain.SetRightSpeed(0.0f);
                return true;

            }
        } else {
            driveTrain.SetBothSpeed(0.0f);
        }

        return false;
    }

    public float Lerp(float v0, float v1, float t) {

        if (t < 0) {
            t = 0;

        } else if (t > 1) {
            t = 1;
        }

        return (v0 + t * (v1 - v0));
    }
}