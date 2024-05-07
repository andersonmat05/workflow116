import java.util.List;

public class EventManager {

    public List<Station> stations;

    public EventManager(List<Station> stations) {
        this.stations = stations;
    }

    public static void getStatus(List<Station> stations) {
        for (Station station : stations) {
            System.out.println("ID of Station: " + station.getStationID());
        }
    }
}