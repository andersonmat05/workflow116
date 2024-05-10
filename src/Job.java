public class Job {
    private final String jobID;
    private final JobType jobType;
    private final int startTime;
    private final int duration;

    public Job(String jobID, JobType jobType, int startTime, int duration) {
        this.jobID = jobID;
        this.jobType = jobType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void printInfo() {
        System.out.print("Job ID: " + jobID);
        System.out.print("  Job Type ID: " + jobType.Id);
        System.out.print("  Job Start Time: " + startTime);
        System.out.println("  Job Duration: " + duration);
    }

    public int getStartTime() {
        return startTime;
    }
}
