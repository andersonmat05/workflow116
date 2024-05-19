public class EventStationEndTask extends EventBase {
    private final Station station;
    private final Task task;

    protected EventStationEndTask(float time, Station station, Task task) {
        super(time);
        this.station = station;
        this.task = task;
    }

    public Station getStation() {
        return station;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "EventStationEndTask{" +
                "station=" + station.getID() +
                ", task=" + task.getID() +
                ", time=" + time +
                '}';
    }

    @Override
    void execute() {
        if (Settings.DEBUG)
            System.out.println("EventStationEndTask for " + task.getID() + " executed at: " + time);

        System.out.printf("Station %s has finished task %s\n", station.getID(), task.getID());

        station.endExecuteTask(task);
        // notify manager to continue event chain
        EventManager.OnStationEndTask(this);
    }
}
