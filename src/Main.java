public class Main {
    public static void main(String[] args) {
        FileManager.parseFiles("workflow.txt", "job.txt");
        EventManager.stations = FileManager.getStations();
        EventManager.getStatus();
    }
}
