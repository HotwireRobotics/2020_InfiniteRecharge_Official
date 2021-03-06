package frc.robot.autostep;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.DriveTrain;

public class TimedForward extends AutoStep {

    public Timer driveTimer;
    public float driveTime;
    public float speed;

    public DriveTrain driveTrain;

    public TimedForward(DriveTrain driveTrain, float driveTime, float speed) {
        super();
        this.driveTime = driveTime;
        this.speed = speed;
        this.driveTrain = driveTrain;
    }

    public void Begin() {
        driveTimer = new Timer();
        driveTimer.reset();
        driveTimer.start();
        driveTrain.SetBothSpeed(speed);
    }

    public void Update() {
        if (driveTimer.get() > driveTime) {
            isDone = true;
            driveTrain.SetBothSpeed(0.0f);
        }
    }
}