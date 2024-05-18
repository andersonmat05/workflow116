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
        // select status
        String status = getTasksInExecution().isEmpty() ? getTasksInQueue().isEmpty() ? "Done" : "Idle" : "Busy";
        return String.format("STATION %s [%s] - Queue: %s  Execution: %s", getID(), status, getTasksInQueue().toString(), getTasksInExecution().toString());
    }

    public String getID() {
        return stationID;
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public Task getNextTask() {
        if (!tasks.isEmpty()) {
            if (fifoFlag)
            {
                // first in first out
                return tasks.getFirst();
            }
            else {
                Task nextTask = tasks.getFirst();
                for (Task task : tasks) {
                    if (task == nextTask) {
                        //skip comparison if same reference
                        continue;
                    }

                    Job taskJob = EventManager.FindJobFromTask(task);
                    Job nextTaskJob = EventManager.FindJobFromTask(nextTask);
                    if (taskJob == null || nextTaskJob == null) {
                        continue;
                    }

                    // compare deadline
                    if (taskJob.getStartTime() + taskJob.getDuration() < nextTaskJob.getStartTime() + nextTaskJob.getDuration()) {
                        nextTask = task;
                    }
                }
                return nextTask;
            }
        }
        if (Settings.DEBUG)
            System.out.println("STATION " + getID() + " has no more tasks");
        return null;
    }

    public ArrayList<Task> getTasksInExecution() {
        return tasksInExecution;
    }

    public ArrayList<Task> getTasksInQueue() {
        return tasksInQueue;
    }

    // move task from queue to execution
    public void beginExecuteTask(Task task) {
        if (tasksInQueue.contains(task)) {
            tasksInExecution.add(task);
            tasksInQueue.remove(task);
        } else {
            if(Settings.DEBUG)
                System.out.println("This task is not assigned to this station");
        }
    }

    // remove task from execution
    public void endExecuteTask(Task task) {
        if (tasksInExecution.contains(task)) {
            tasksInExecution.remove(task);
        } else {
            if(Settings.DEBUG)
                System.out.println("This task is not assigned to this station");
        }
    }
}
