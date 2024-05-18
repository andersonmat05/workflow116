import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        if (!FileManager.parseFiles("workflow.txt", "job.txt")) {
            // End program if there were errors.
            return;
        }

        EventManager.Init(FileManager.getStations(), FileManager.getJobs());

        Scanner sc = new Scanner(System.in);
        for (Station s : FileManager.getStations()) {
            System.out.println(s.getStatus());
        }
        while (EventManager.hasNextEvent()) {
            EventBase event = EventManager.nextEvent();
            event.execute();

            for (Station s : FileManager.getStations()) {
                System.out.println(s.getStatus());
            }

            System.out.println("Events remaining: " + EventManager.getEventQueue().toArray().length);
            EventManager.removeEvent(event);
        }

        System.out.println("Execution finished");
    }
}
