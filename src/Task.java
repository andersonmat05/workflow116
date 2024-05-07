public final class Task {
    public final TaskType type;
    public final float size;

    // Use default size
    public Task(TaskType type) {
        this(type, type.defaultSize);
    }

    // Override default size
    public Task(TaskType type, float size) {
        this.type = type;
        this.size = size;
    }
}
