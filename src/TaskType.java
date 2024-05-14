public final class TaskType {
    public final String taskTypeId;
    public final float defaultSize;

    public TaskType(String taskTypeId) {
        this(taskTypeId, 1.f);
    }

    public TaskType(String taskTypeId, float defaultSize) throws IllegalArgumentException {
        if (taskTypeId.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        if (defaultSize <= 0) {
            throw new IllegalArgumentException("defaultSize must be greater than zero");
        }

        this.taskTypeId = taskTypeId;
        this.defaultSize = defaultSize;
    }

    @Override
    public String toString() {
        return "TaskType{" +
                "taskTypeId='" + taskTypeId + '\'' +
                ", defaultSize=" + defaultSize +
                '}';
    }

    public String getTaskId() {
        return taskTypeId;
    }
}
