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

    @Override
    public String toString() {
        return "EventStationEndTask{" +
                "station=" + station.getStationID() +
                ", task=" + task.getID() +
                ", time=" + time +
                '}';
    }

    @Override
    void execute() {
        if (Settings.DEBUG)
            System.out.println("EventStationEndTask for " + task.getID() + " executed at: " + time);

        station.endExecuteTask(task);
        // notify manager to continue event chain
        EventManager.OnStationEndTask(this);
    }
}
