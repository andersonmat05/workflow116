import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (!FileManager.parseFiles("workflow.txt", "job.txt")) {
            // End program if there were errors.
            System.out.println("\nAborting execution.");
        } else {

            // array used for cosmetic purposes
            ArrayList<Station> stations = sortStations(FileManager.getStations());

            for (Station s : stations) {
                System.out.println("Station " + s.getID() + ": " + Arrays.toString(s.getTasks().toArray()));
            }

            EventManager.Init();

            while (EventManager.hasNextEvent()) {
                EventManager.executeNextEvent();
                System.out.println("\nTime: " + EventManager.getTime());

                for (Station s : stations) {
                    System.out.println(s.getStatus());
                }

                if (Settings.DEBUG)
                    System.out.println("Events remaining: " + Arrays.toString(EventManager.getEventQueue().toArray()));
            }

            System.out.println("\nExecution finished. Total time: " + EventManager.getTime());

            float totalTardiness = 0.f;
            for (Station s : stations) {
                System.out.println("Station " + s.getID() + " tardiness: " + s.getAverageTardiness());
                totalTardiness += s.getAverageTardiness();
            }
            System.out.println("Average tardiness: " + totalTardiness / FileManager.getStations().size());
        }
    }

    // Sorts set of stations alphabetically based on ids
    private static ArrayList<Station> sortStations(Set<Station> InStations) {
        ArrayList<Station> stations = new ArrayList<>(InStations);
        ArrayList<Station> sortedStations = new ArrayList<>();
        final int stationCount = stations.size();
        for (int i = 0; i < stationCount; i++) {
            Station firstStation = stations.getFirst();
            for (int j = 1; j < stations.size(); j++) {
                if (stations.get(j).getID().compareTo(firstStation.getID()) < 0) {
                    firstStation = stations.get(j);
                }
            }
            stations.remove(firstStation);
            sortedStations.add(firstStation);
        }
        return sortedStations;
    }
}
