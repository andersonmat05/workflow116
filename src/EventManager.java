import java.util.ArrayList;
import java.util.Random;

public class EventManager {
    public static ArrayList<Station> stations;

    public static void WorkFlow() {
        ArrayList<Station> stations = FileManager.getStations();
        Random rn = new Random(System.currentTimeMillis());

        float plusminus; // = rn.nextFloat();
        int plusorminus;
        float Size;

        // Assume float below as start time for jobs
        float starttime = 1.0f;

        for (Station station : stations) {
            float uppersize = 0.0f;
            if (station.isMultiFlag() == true) {
                for (Task task : station.getTasks()) {
                    System.out.print("StationID: " + station.getStationID() + "  ");

                    if (task.plusMinus != 0) {
                        plusorminus = rn.nextInt(2);
                        // change plusminus's bound according to report
                        plusminus = rn.nextFloat(task.plusMinus);

                        if (plusorminus == 2) {
                            plusminus = -plusminus;
                        }

                        Size = task.getSize() + (task.getSize() * plusminus) / 100;
                    } else {
                        Size = task.getSize();
                    }
                    System.out.println("TaskID: " + task.getID() + " ends at: " + (starttime + Size));
                }
            } else {
                for (Task task : station.getTasks()) {
                    System.out.print("StationID: " + station.getStationID() + "  ");

                    if (task.plusMinus != 0) {
                        plusorminus = rn.nextInt(2);
                        // change plusminus's bound according to report
                        plusminus = rn.nextFloat(task.plusMinus);

                        if (plusorminus == 2) {
                            plusminus = -plusminus;
                        }

                        Size = task.getSize() + (task.getSize() * plusminus) / 100 + uppersize;
                    } else {
                        Size = task.getSize() + uppersize;
                    }
                    uppersize = Size;
                    System.out.println("TaskID: " + task.getID() + " ends at: " + (starttime + Size));
                }
            }
        }
    }
}