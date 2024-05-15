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
                "station=" + station +
                ", task=" + task +
                '}';
    }

    @Override
    void execute() {
        System.out.println(this);
        station.endExecuteTask(task);
    }
}
