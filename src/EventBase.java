public abstract class EventBase implements Comparable<EventBase> {
    public final float time;

    protected EventBase(float time) {
        this.time = time;
    }

    abstract void execute();

    @Override
    public int compareTo(EventBase o) {
        return Float.compare(time, o.time);
    }
}
