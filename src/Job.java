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

    @Override
    public String toString() {
        return "Job{" +
                "jobID='" + jobID + '\'' +
                ", jobType=" + jobType +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return startTime + duration;
    }

    public String getJobID() {
        return jobID;
    }

    public JobType getJobType() {
        return jobType;
    }
}
