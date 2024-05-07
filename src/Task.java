public final class Task {
    public final TaskType taskType;
    public final float size;
    public final float plusMinus;

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
        this.size = size;
        this.plusMinus = plusMinus;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskType=" + taskType.type +
                ", size=" + size +
                ", plusMinus=" + plusMinus +
                '}';
    }
}
