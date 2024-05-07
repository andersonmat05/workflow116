public final class TaskType {
    public final String type;
    public final float defaultSize;

    public TaskType(String type) {
        this(type, 1.f);
    }

    public TaskType(String type, float defaultSize) throws IllegalArgumentException {
        if (type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        if (defaultSize <= 0) {
            throw new IllegalArgumentException("defaultSize must be greater than zero");
        }

        this.type = type;
        this.defaultSize = defaultSize;
    }
}
