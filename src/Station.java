import java.util.ArrayList;

public class Station {
    private final String stationID;
    private final int capacity;
    private final boolean multiFlag;
    private final boolean fifoFlag;
    private final ArrayList<Task> tasks;

    private ArrayList<Task> tasksInExecution = new ArrayList<>();

    public Station(String stationID, int capacity, boolean multiFlag, boolean fifoFlag, ArrayList<Task> tasks) {
        this.stationID = stationID;
        this.capacity = capacity;
        this.multiFlag = multiFlag;
        this.fifoFlag = fifoFlag;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationID='" + stationID + '\'' +
                ", capacity=" + capacity +
                ", multiFlag=" + multiFlag +
                ", fifoFlag=" + fifoFlag +
                ", tasks=" + tasks.toString() +
                '}';
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

    public boolean isMultiFlag() {
        return multiFlag;
    }

    public boolean isFifoFlag() {
        return fifoFlag;
    }

    public ArrayList<Task> getTasksInExecution() {
        return tasksInExecution;
    }

    public boolean executeTask(Task task) {
        if (tasks.contains(task)) {
            tasksInExecution.add(task);
            return true;
        } else {
            System.out.println("This task is not assigned to this station");
            return false;
        }
    }
}
