import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        if (!FileManager.parseFiles("workflow.txt", "job.txt")) {
            // End program if there were errors.
            return;
        }

        EventManager.Init(FileManager.getStations(), FileManager.getJobs());

        Scanner sc = new Scanner(System.in);
        while (EventManager.hasNextEvent()) {
            EventBase event = EventManager.nextEvent();
            event.execute();
            System.out.println("Events remaining: " + EventManager.getEventQueue().toArray().length);
            System.out.println("\nEnter anything to continue.. ");
            sc.next();
            EventManager.removeEvent(event);
        }

        System.out.println("Execution finished");
    }
}
