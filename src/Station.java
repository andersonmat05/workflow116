import java.util.List;

public class Station {
    private final String stationID;
    private final int capacity;
    private final float speed;
    private List<String> taskTypes;

    public Station(String stationID, int capacity, float speed, List<String> taskTypes) {
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
}
