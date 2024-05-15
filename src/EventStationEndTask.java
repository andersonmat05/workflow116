public class EventStationEndTask extends EventBase {
    private final Station station;
    private final Task task;

    protected EventStationEndTask(float time, Station station, Task task) {
        super(time);
        this.station = station;
        this.task = task;
    }

    @Override
    public String toString() {
        return "EventStationEndTask{" +
                "station=" + station +
                ", task=" + task +
                '}';
    }

    @Override
    void execute() {
        System.out.println(this);
        station.beginExecuteTask(task);
    }
}
