public class NullEvent implements EventInterface {

    @Override
    public void execute() {
        System.out.println("Null event executed at: " + executionTime);
    }

    @Override
    public void report() {
        System.out.println("Null event reported.");
    }

    private final float executionTime;

    public NullEvent(float executionTime) {
        System.out.println("Null event queued: " + executionTime);
        this.executionTime = executionTime;
    }
}
