import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        if (!FileManager.parseFiles("workflow.txt", "job.txt")) {
            // End program if there were errors.
            return;
        }
        for (Station s : FileManager.getStations()) {
            System.out.println(s.toString());
        }
        /*
        EventManager.Init(FileManager.getStations());

        Scanner sc = new Scanner(System.in);
        while (EventManager.hasNextEvent()) {
            EventInterface event = EventManager.nextEvent();
            event.report();
            System.out.println("\nEnter anything to continue.. ");
            sc.next();
            EventManager.executeNextEvent();
        }

        System.out.println("Execution finished");

         */
    }
}
