public class EventStationBeginTask extends EventBase {
    private final Station station;
    private final Task task;

    protected EventStationBeginTask(float time, Station station, Task task) {
        super(time);
        this.station = station;
        this.task = task;
    }

    @Override
    public String toString() {
        return "EventStationBeginTask{" +
                "station=" + station.getStationID() +
                ", task=" + task.getID() +
                ", time=" + time +
                '}';
    }

    @Override
    void execute() {
        if (Settings.DEBUG)
            System.out.println("EventStationBeginTask for " + task.getID() + " executed at: " + time);

        station.beginExecuteTask(task);
    }
}
