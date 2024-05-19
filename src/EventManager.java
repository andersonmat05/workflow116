import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class EventManager {

    private static boolean IsInitialized = false;
    private static final ArrayList<EventBase> eventQueue = new ArrayList<EventBase>();
    private static float Time;

    /** Returns the last time an event has executed. */
    public static float getTime() {
        return Time;
    }

    public static boolean getIsInitialized() {
        return IsInitialized;
    }

    /** Set up critical data for the manager and assign initial tasks to stations. */
    public static void Init() {
        IsInitialized = true;
        eventQueue.clear();
        Time = 0.f;

        // Init stations
        for (Station station : FileManager.getStations()) {
            // clear arrays
            station.getTasksInQueue().clear();
            station.getTasksInExecution().clear();
            // initialize first task to start event chain
            AssignTasks(station);
        }

        Collections.sort(eventQueue);
    }

    public static void AssignTasks(Station station) {
        // loop until capacity is full or there is no more unassigned task
        while (station.getTasksInQueue().toArray().length <= station.getCapacity()
                && station.getTasks().toArray().length > 0) {

            // grab the next task
            Task task = station.getNextTask();
            Job taskJob = FileManager.FindJobFromTask(task);

            if (task != null && taskJob != null) {
                // move new task to queue from task list
                station.getTasksInQueue().add(task);
                station.getTasks().remove(task);

                // calculate duration from task size and job duration
                final float startTime = Math.max(taskJob.getStartTime(), getTime()); // start immediately or wait until job starts
                final float endTime = startTime + task.getSize();

                // create start and end events for the task
                AddEvent(new EventStationBeginTask(taskJob.getStartTime(), station, task));
                AddEvent(new EventStationEndTask(endTime, station, task));
                if (Settings.DEBUG)
                    System.out.printf("[%s] Task %s assigned to %s, start: %f end: %f\n", EventManager.class.getName(), task.getID(), station.getID(), startTime, endTime);
            }
        }
    }

    public static void OnStationBeginTask(EventStationBeginTask event) {
        Time = event.time;
    }

    public static void OnStationEndTask(EventStationEndTask event) {
        Time = event.time;

        // record tardiness
        Job eventJob = FileManager.FindJobFromTask(event.getTask());
        assert eventJob != null;
        final float deadline = eventJob.getStartTime() + eventJob.getDuration();
        if (Time > deadline) {
            event.getStation().addTardiness(Time - deadline);
        }

        AssignTasks(event.getStation());
    }

    public static void AddEvent(EventBase event) {
        eventQueue.add(event);
    }

    public static boolean hasNextEvent() {
        if (!getIsInitialized()) return false;
        return !eventQueue.isEmpty();
    }

    public static EventBase nextEvent() {
        return eventQueue.getFirst();
    }

    public static void executeNextEvent() {
        EventBase event = nextEvent();
        removeEvent(event);
        event.execute();

    }

    public static void removeEvent(EventBase event) {
        eventQueue.remove(event);
    }

    public static ArrayList<EventBase> getEventQueue() {
        return eventQueue;
    }
}