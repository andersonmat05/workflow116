import java.util.ArrayList;

public class Station {
    private final String stationID;
    private final int capacity;
    private final boolean multiFlag;
    private final boolean fifoFlag;
    private ArrayList<Task> tasks;

    public Station(String stationID, int capacity, boolean multiFlag, boolean fifoFlag, ArrayList<Task> tasks) {
        this.stationID = stationID;
        this.capacity = capacity;
        this.multiFlag = multiFlag;
        this.fifoFlag = fifoFlag;
        this.tasks = tasks;
    }

    public String getStationID() {
        return stationID;
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public boolean isMultiFlag() { return multiFlag; }
}
