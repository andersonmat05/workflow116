package src;
import java.util.List;
public class Station {
    private String stationID;
    private int capacity;
    private double speed;
    List<String> taskTypes;

    public Station(String stationID, int capacity, double speed, List<String> taskTypes) {
        this.stationID = stationID;
        this.capacity = capacity;
        this.speed = speed;
        this.taskTypes = taskTypes;
    }
    public String getStationID() {
        return stationID;
    }
    public int getCapacity() {
        return capacity;
    }
    public double getSpeed() {
        return speed;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
