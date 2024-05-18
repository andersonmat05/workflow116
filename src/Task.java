import java.util.Random;

public final class Task {
    // Params
    public final TaskType taskType;
    public final float size;

    // Use default size
    public Task(TaskType taskType) {
        this(taskType, taskType.defaultSize);
    }

    // Override default size
    public Task(TaskType taskType, float size) {
        this(taskType, size, 0);
    }

    // Full constructor
    public Task(TaskType taskType, float size, float plusMinus) {
        this.taskType = taskType;

        if (plusMinus != 0) {
            Random rand = new Random(System.currentTimeMillis());
            plusMinus = rand.nextFloat(plusMinus);
            plusMinus *= rand.nextBoolean() ? 1.0f : -1.0f;
            this.size = size + plusMinus;
        } else {
            this.size = size;
        }

    }

    @Override
    public String toString() {
        return taskType.getTaskId();
    }

    public float getSize() {
        return size;
    }

    public String getID() {
        return taskType.getTaskId();
    }

}
