public final class TaskType {
    public final String Id;
    public final float defaultSize;

    public TaskType(String Id) {
        this(Id, 1.f);
    }

    public TaskType(String Id, float defaultSize) throws IllegalArgumentException {
        if (Id.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        if (defaultSize <= 0) {
            throw new IllegalArgumentException("defaultSize must be greater than zero");
        }

        this.Id = Id;
        this.defaultSize = defaultSize;
    }

    @Override
    public String toString() {
        return "TaskType{" +
                "taskTypeId='" + Id + '\'' +
                ", defaultSize=" + defaultSize +
                '}';
    }

    public String getTaskId() {
        return Id;
    }
}
