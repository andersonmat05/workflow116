import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        if (!FileManager.parseFiles("workflow.txt", "job.txt")) {
            // End program if there were errors.
            System.out.println("\nAborting execution.");
            return;
        }

        EventManager.Init(FileManager.getStations(), FileManager.getJobs());

        while (EventManager.hasNextEvent()) {
            EventManager.executeNextEvent();
            System.out.println("\nTime: " + EventManager.getTime());
            for (Station s : FileManager.getStations()) {
                System.out.println(s.getStatus());
            }
            System.out.println("Events remaining: " + Arrays.toString(EventManager.getEventQueue().toArray()));
        }

        System.out.println("\nExecution finished.");
    }
}
