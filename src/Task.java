public final class Task {
    public static final float defaultSize = 1.f;

    public final String type;
    public final float size;

    public Task(String type) {
        this(type, defaultSize);
    }

    public Task(String type, float size) {
        this.type = type;
        this.size = size;
    }
}
