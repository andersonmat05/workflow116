import java.util.ArrayList;

public class Station {
    private final String stationID;
    private final int capacity;
    private final boolean multiFlag;
    private final boolean fifoFlag;
    private final ArrayList<Task> tasks;

    private final ArrayList<Task> tasksInExecution = new ArrayList<>();
    private final ArrayList<Task> tasksInQueue = new ArrayList<>();

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

    public String getStatus() {
        return String.format("STATION %s - Queue: %s  Execution: %s", getStationID(), getTasksInQueue().toString(), getTasksInExecution().toString());
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

    public ArrayList<Task> getTasksInQueue() {
        return tasksInQueue;
    }

    public boolean isIdle() {
        return tasksInExecution.isEmpty();
    }

    public boolean isDone() {
        return tasksInQueue.isEmpty() && tasksInExecution.isEmpty();
    }

    public int getFreeCapacity() {
        System.out.println(capacity - tasksInExecution.size());
        return capacity - tasksInExecution.size();
    }

    // move task from queue to execution
    public boolean beginExecuteTask(Task task) {
        if (tasksInQueue.contains(task)) {
            tasksInExecution.add(task);
            tasksInQueue.remove(task);
            return true;
        } else {
            System.out.println("This task is not assigned to this station");
            return false;
        }
    }

    // remove task from execution
    public boolean endExecuteTask(Task task) {
        if (tasksInExecution.contains(task)) {
            tasksInExecution.remove(task);
            return true;
        } else {
            System.out.println("This task is not assigned to this station");
            return false;
        }
    }
}
