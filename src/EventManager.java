import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class EventManager {

    private static boolean IsInitialized = false;
    private static final ArrayList<EventBase> eventQueue = new ArrayList<EventBase>();
    private static Set<Job> jobs;
    private static float Time;

    /** Returns the last time an event has executed. */
    public static float getTime() {
        return Time;
    }

    /** Set up critical data for the manager and assign initial tasks to stations. */
    public static void Init(Set<Station> stations, Set<Job> InJobs) {
        IsInitialized = true;
        eventQueue.clear();
        jobs = InJobs;
        Time = 0.f;

        // Init stations
        for (Station station : stations) {
            // clear arrays
            station.getTasksInQueue().clear();
            station.getTasksInExecution().clear();
            // initialize first task to start event chain
            AssignTasks(station);
        }

        Collections.sort(eventQueue);
    }

    public static boolean getIsInitialized() {
        return IsInitialized;
    }

    public static void AssignTasks(Station station) {
        // loop until capacity is full or there is no more unassigned task
        while (station.getTasksInQueue().toArray().length <= station.getCapacity()
                && station.getTasks().toArray().length > 0) {

            // grab the next task
            Task newTask = station.getNextTask();
            Job taskJob = FindJobFromTask(newTask);

            if (newTask != null && taskJob != null) {
                // move new task to queue from task list
                station.getTasksInQueue().add(newTask);
                station.getTasks().remove(newTask);

                // calculate duration from task size and job duration
                final float startTime = Math.min(taskJob.getStartTime(), getTime()); // start immediately or wait until job starts
                final float endTime = startTime + newTask.getSize();

                // create start and end events for the task
                AddEvent(new EventStationBeginTask(taskJob.getStartTime(), station, newTask));
                AddEvent(new EventStationEndTask(endTime, station, newTask));
            }
        }
    }

    public static void OnStationBeginTask(EventStationBeginTask event) {
        Time = event.time;
    }

    public static void OnStationEndTask(EventStationEndTask event) {
        Time = event.time;
        AssignTasks(event.getStation());
    }

    public static void AddEvent(EventBase event) {
        if (Settings.DEBUG)
            System.out.println("New event: " + event.toString());

        eventQueue.add(event);
    }

    public static Job FindJobFromTask(Task task) {
        if (!getIsInitialized())
        {
            System.out.println("Event Manager is not initialized yet.");
            return null;
        }

        for (Job job : jobs) {
            for (Task jobTask : job.getJobType().getTasks()) {
                if (jobTask.taskType.getTaskId().equals(task.taskType.taskTypeId)) {
                    return job;
                }
            }
        }
        return null;
    }

    public static Job FindJobFromId(String Id) {
        if (!getIsInitialized())
        {
            System.out.println("Event Manager is not initialized yet.");
            return null;
        }

        for (Job job : jobs) {
            if (job.getJobID().equals(Id)) {
                return job;
            }
        }
        if (Settings.DEBUG)
            System.out.println("FindJobFromId failed.");
        return null;
    }

    public static ArrayList<Station> stations;

    public static boolean hasNextEvent() {
        if (!getIsInitialized()) return false;
        return !eventQueue.isEmpty();
    }

    public static EventBase nextEvent() {
        return eventQueue.getFirst();
    }

    public static void executeNextEvent() {
        EventBase event = nextEvent();
        event.execute();
        eventQueue.removeFirst();
    }

    public static void removeEvent(EventBase event) {
        eventQueue.remove(event);
    }

    public static ArrayList<EventBase> getEventQueue() {
        return eventQueue;
    }
}