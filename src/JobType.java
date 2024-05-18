import java.util.ArrayList;

public final class JobType {
    public final String Id;
    private final ArrayList<Task> tasks;

    public JobType(String Id, ArrayList<Task> tasks) throws IllegalArgumentException {
        if (Id.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }

        this.Id = Id;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "JobType{" +
                "Id='" + Id + '\'' +
                ", tasks=" + tasks +
                '}';
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
