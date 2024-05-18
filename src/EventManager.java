import java.util.*;

public class EventManager {

    private static ArrayList<EventBase> eventQueue = new ArrayList<EventBase>();

    private static Set<Job> jobs;

    public static void Init(Set<Station> stations, Set<Job> InJobs) {
        jobs = InJobs;
        eventQueue.clear();

        for (Station station : stations) {
            // Add all assigned tasks to queue
            station.getTasksInQueue().clear();
            station.getTasksInExecution().clear();

            // initialize first task
            AssignTasks(station);


            // loop through tasks until station is done


            /*
            while (!station.isDone()) {
                System.out.println("Station not done: " + station);
                // start the next task if station is idle
                if (station.isIdle()) {

                } else {
                    System.out.println(station.getTasksInExecution().toString());
                }
            }
            */
        }

        Collections.sort(eventQueue);
    }

    public static void AssignTasks(Station station) {
        // loop until capacity is full or there is no more unassigned task
        while (station.getTasksInQueue().toArray().length < station.getCapacity()
                && station.getTasks().toArray().length > 0) {
            // grab the first unassigned task
            Task newTask = station.getTasks().getFirst();
            Job taskJob = FindJobFromTask(newTask);

            if (newTask != null && taskJob != null) {
                // move new task to queue from task list
                station.getTasksInQueue().add(newTask);
                station.getTasks().remove(newTask);

                // calculate duration from task size and job duration
                final float startTime = taskJob.getStartTime();
                final float endTime = (taskJob.getDuration()/taskJob.getTotalTaskSize())*newTask.getSize() + taskJob.getStartTime();

                // create start and end events for the task
                AddEvent(new EventStationBeginTask(taskJob.getStartTime(), station, newTask));
                AddEvent(new EventStationEndTask(endTime, station, newTask));
            }
        }
    }

    public static void OnStationEndTask(EventStationEndTask event) {
        AssignTasks(event.getStation());
    }

    public static void AddEvent(EventBase event) {
        if (Settings.DEBUG)
            System.out.println("New event: " + event.toString());

        eventQueue.add(event);
    }

    private static Job FindJobFromTask(Task task) {
        for (Job job : jobs) {
            for (Task jobTask : job.getJobType().getTasks()) {
                if (jobTask.taskType.getTaskId().equals(task.taskType.taskTypeId)) {
                    return job;
                }
            }
        }
        return null;
    }

    private static Job FindJobFromId(String Id) {
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