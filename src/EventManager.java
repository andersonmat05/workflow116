import java.util.*;

public class EventManager {

    private static ArrayList<EventBase> eventQueue = new ArrayList<EventBase>();

    public static void Init(Set<Station> stations, Set<Job> jobs) {
        eventQueue.clear();

        for (Station station : stations) {
            // Add all assigned tasks to queue
            station.getTasksInQueue().clear();
            station.getTasksInExecution().clear();

            //todo: move tasks one by one in while to check whether assignment is done or not
            station.getTasksInQueue().addAll(station.getTasks());

            // loop through tasks until station is done
            while (!station.isDone()) {
                System.out.println("Station not done: " + station);
                // start the next task if station is idle
                if (station.isIdle()) {
                    while (station.getFreeCapacity() > 0) {
                        Task newTask = station.getTasksInQueue().getFirst();
                        Job taskJob = FindJobFromTask(jobs, newTask);
                        if (newTask != null && taskJob != null) {
                            // create start and end events for the task
                            AddEvent(new EventStationBeginTask(taskJob.getStartTime(), station, newTask));
                            // calculate duration from task size and job duration
                            final float endTime = (taskJob.getDuration()/taskJob.getTotalTaskSize())*newTask.getSize() + taskJob.getStartTime();
                            AddEvent(new EventStationEndTask(endTime, station, newTask));
                        } else {
                            System.out.println("new task or findjobfromtask failed");
                        }
                    }
                } else {
                    System.out.println(station.getTasksInExecution().toString());
                }
            }
        }

        Collections.sort(eventQueue);
    }

    public static void AddEvent(EventBase event) {
        System.out.println(event);
        eventQueue.add(event);
    }

    private static Job FindJobFromTask(Set<Job> jobs, Task task) {
        for (Job job : jobs) {
            for (Task jobTask : job.getJobType().getTasks()) {
                if (jobTask.taskType.getTaskId().equals(task.taskType.taskTypeId)) {
                    return job;
                }
            }
        }
        return null;
    }

    private static Job FindJobFromId(String Id, Set<Job> jobs) {
        for (Job job : jobs) {
            if (job.getJobID().equals(Id)) {
                return job;
            }
        }
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