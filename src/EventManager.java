import java.util.ArrayList;
import java.util.Calendar;

public class EventManager {
    public static ArrayList<Station> stations;

    public static void WorkFlow() {

        Calendar cal = Calendar.getInstance();
        int hours = cal.get(Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);

        ArrayList<Task> tasks = null;

        boolean Track = false;

        while (!Track) {
            for (Station station : stations) {
                if (0 < hours){}
                tasks = station.getTasks();
            }
        }

    }

    public static void getStatus() {
        for (Station station : stations) {
            System.out.println("ID of Station: " + station.getStationID());
        }
    }
}