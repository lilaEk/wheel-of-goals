import java.awt.*;

class Goal {
    private String nameOfGoal;
    private Color color;
    private int progress;
    private double startAngle;
    private double angle;

    public Goal(double startAngle, double angle, Color color) {
        this.startAngle = startAngle;
        this.angle = angle;
        this.color = color;
    }

    public String getNameOfGoal() {
        return nameOfGoal;
    }

    public void setNameOfGoal(String nameOfGoal) {
        this.nameOfGoal = nameOfGoal;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}