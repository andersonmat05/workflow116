import java.util.ArrayList;

public final class JobType {
    public final String Id;
    public final ArrayList<Task> tasks;

    public JobType(String Id) {
        this(Id, new ArrayList<>());
    }

    public JobType(String Id, ArrayList<Task> tasks) throws IllegalArgumentException {
        if (Id.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }

        this.Id = Id;
        this.tasks = tasks;
    }
}
