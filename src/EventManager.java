import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class EventManager {

    private static ArrayList<EventInterface> eventQueue = new ArrayList<EventInterface>();

    public static void Init(Set<Station> stations) {

    }

    private static void InitStation(Station station) {

    }

    public static ArrayList<Station> stations;

    public static void InitStations() {

    }

    public static boolean hasNextEvent() {
        return !eventQueue.isEmpty();
    }

    public static EventInterface nextEvent() {
        return eventQueue.getFirst();
    }

    public static void executeNextEvent() {
        EventInterface event = nextEvent();
        event.execute();
        eventQueue.removeFirst();
    }

    public static ArrayList<EventInterface> getEventQueue() {
        return eventQueue;
    }
}