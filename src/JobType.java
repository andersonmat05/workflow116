import java.util.ArrayList;

public final class JobType {
    public final String type;
    public final ArrayList<Task> tasks;

    public JobType(String type) {
        this(type, new ArrayList<>());
    }

    public JobType(String type, ArrayList<Task> tasks) throws IllegalArgumentException {
        if (type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }

        this.type = type;
        this.tasks = tasks;
    }
}
