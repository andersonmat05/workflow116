import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileManager {

    // Regex
    public static final String REGEX_SECTION = "^[A-Z]+$";
    public static final String REGEX_TASK = "^(T)(_)?[0-9]+$";
    public static final String REGEX_JOB = "^(J)(_)?[0-9]+$";
    public static final String REGEX_STATION = "^(S)(_)?[0-9]+$";
    public static final String REGEX_FLAG = "^[YN]$";
    public static final String REGEX_FLOAT = "^([0-9]*[.])?[0-9]+$";

    // Errors
    private static final ArrayList<FileErrorException> jobFileErrors = new ArrayList<>();
    private static final ArrayList<FileErrorException> workflowFileErrors = new ArrayList<>();

    // Data
    private static final Set<TaskType> taskTypes = new HashSet<>();
    private static final Set<JobType> jobTypes = new HashSet<>();
    private static final Set<Station> stations = new HashSet<>();
    private static final Set<Job> jobs = new HashSet<>();

    // Parser globals
    private static WorkflowSection currentSection;
    private static int taskParserIterator; // used global field instead of defining mutable int class just for one function

    private enum WorkflowSection {
        INVALID_SECTION,
        TASKTYPES,
        JOBTYPES,
        STATIONS
    }

    public static Set<Station> getStations() {
        return stations;
    }

    private static class TaskTypeNotFoundException extends RuntimeException {
    }

    /** Parses both files and handles assignments, then reports errors */
    public static boolean parseFiles(String workflowFile, String jobFile) {
        boolean success = true;

        // Parse workflow file
        try {
            parseWorkflowFile(workflowFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error reading workflow file: " + e.getMessage());
            success = false;
        }

        // Parse job file
        try {
            jobs.clear();
            jobs.addAll(parseJobFile(jobFile));
        } catch (FileNotFoundException e) {
            System.out.println("Error reading job file: " + e.getMessage());
            success = false;
        }
        if (success)
            return reportErrors();
        return false;
    }

    /**
     * Parses each line of the job file and return created job objects
     **/
    private static ArrayList<Job> parseJobFile(String jobFile)
            throws FileNotFoundException {
        ArrayList<Job> jobs = new ArrayList<>();
        Scanner sc = new Scanner(new File(jobFile));
        int lineIndex = 0;
        while (sc.hasNextLine()) {
            try {
                Job newJob = parseJobLine(sc.nextLine(), ++lineIndex);
                // skip line if null
                if (newJob != null) {
                    jobs.add(newJob);
                }
            } catch (FileErrorException e) {
                jobFileErrors.add(e);
            }
        }
        sc.close();
        return jobs;
    }

    /** Parses workflow file and assign all objects **/
    private static void parseWorkflowFile(String jobFile) throws FileNotFoundException {
        // file not found thrown here
        Scanner sc = new Scanner(new File(jobFile));
        currentSection = WorkflowSection.INVALID_SECTION;
        int lineIndex = 0;

        // populate section map by indexing file contents
        while (sc.hasNextLine()) {
            lineIndex++;
            try {
                // read line
                String line = sc.nextLine().trim();
                // remove whitespace
                line = line.replaceAll("\\s+", " ");
                // remove parentheses
                line = line.replaceAll("[(|)]", "");
                String[] lineElements = line.split(" ");
                // skip blank line
                if (lineElements.length == 0) {
                    continue;
                }

                // parse line
                ArrayList<Object> parsedObjects = parseWorkflowLine(lineElements, lineIndex);

                // quick fix for not having abstract parent for our objects
                assert parsedObjects != null;
                for (Object parsedObject : parsedObjects) {
                    if (parsedObject instanceof final TaskType taskType) {
                        taskTypes.add(taskType);
                    }
                    if (parsedObject instanceof final JobType jobType) {
                        jobTypes.add(jobType);
                    }
                    if (parsedObject instanceof final Station station) {
                        stations.add(station);
                    }
                }

            } catch (FileErrorException e) {
                workflowFileErrors.add(e);
            }
        }
        sc.close();
    }

    /**
     * @param lineElements Array of elements in the line to be parsed
     * @param lineIndex    Used for error reporting
     */
    private static ArrayList<Object> parseWorkflowLine(String[] lineElements, int lineIndex) throws FileErrorException {
        ArrayList<Object> parsedObjects = new ArrayList<>();

        // iterate elements
        for (int i = 0; i < lineElements.length; i++) {
            // look for section
            if (lineElements[i].trim().matches(REGEX_SECTION)) {
                try {
                    WorkflowSection newSection = WorkflowSection.valueOf(lineElements[i].trim());

                    if (newSection == WorkflowSection.INVALID_SECTION) {
                        throw new FileErrorException(lineIndex,
                                FileErrorException.ExceptionCause.SECTION_IDENTIFIER_INVALID);
                    }

                    // ensure TASKTYPES is the first section in the file
                    if (currentSection == WorkflowSection.INVALID_SECTION && newSection != WorkflowSection.TASKTYPES) {
                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASKTYPES_NOT_FIRST);
                    }

                    // Assign new section
                    currentSection = newSection;
                } catch (IllegalArgumentException e) {
                    // enum conversion failed
                    throw new FileErrorException(lineIndex,
                            FileErrorException.ExceptionCause.SECTION_IDENTIFIER_INVALID);
                }
            } else {
                // ensure current section
                if (currentSection == WorkflowSection.INVALID_SECTION) {
                    // something gone wrong, abort func
                    return null;
                }

                switch (currentSection) {
                    case TASKTYPES:
                        // ensure syntax
                        if (lineElements[i].trim().matches(REGEX_TASK)) {
                            // check if there is next element
                            if (lineElements.length > i + 1) {
                                // check if positive float
                                String nextElement = lineElements[i + 1].trim();
                                if (nextElement.matches(REGEX_FLOAT)) {
                                    // add to types
                                    taskTypes.add(new TaskType(lineElements[i].trim(), Float.parseFloat(nextElement)));
                                    i++; // skip to next element
                                    continue;
                                } else {
                                    if (!nextElement.matches(REGEX_SECTION) && !nextElement.matches(REGEX_TASK)) {
                                        throw new FileErrorException(lineIndex,
                                                FileErrorException.ExceptionCause.TASK_SIZE_INVALID);
                                    }
                                }
                            }
                            // No size defined, construct default tasktype
                            taskTypes.add(new TaskType(lineElements[i]));
                        } else {
                            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASK_INVALID);
                        }
                        break;
                    case JOBTYPES:
                        if (lineElements[i].trim().matches(REGEX_JOB)) {
                            String jobId = lineElements[i].trim();
                            ArrayList<Task> tasks = new ArrayList<>();
                            // use global here
                            taskParserIterator = i;
                            while (lineElements.length > taskParserIterator + 1) {
                                taskParserIterator++;
                                // break loop if not job args
                                if (!lineElements[taskParserIterator].trim().matches(REGEX_TASK)
                                        && !lineElements[taskParserIterator].trim().matches(REGEX_FLOAT)) {
                                    break;
                                }
                                // look for task definitions
                                tasks.add(parseTask(lineElements, lineIndex, false));
                            }
                            // return to local
                            i = taskParserIterator;
                            taskParserIterator = -1;

                            parsedObjects.add(new JobType(jobId, tasks));
                        } else {
                            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.JOB_INVALID);
                        }
                        break;
                    case STATIONS:
                        if (lineElements[i].trim().matches(REGEX_STATION)) {
                            if (lineElements.length < 5) {
                                throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.ARGS_FEW);
                            }

                            String stationId = lineElements[i].trim();
                            int stationCapacity;
                            boolean multiFlag;
                            boolean fifoFlag;

                            try {
                                stationCapacity = Integer.parseInt(lineElements[i + 1].trim());
                            } catch (NumberFormatException e) {
                                throw new FileErrorException(lineIndex,
                                        FileErrorException.ExceptionCause.STATION_CAPACITY_INVALID);
                            }
                            if (lineElements[i + 2].trim().matches(REGEX_FLAG)) {
                                multiFlag = lineElements[i + 2].trim().equalsIgnoreCase("Y");
                            } else {
                                throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.STATION_FLAG_INVALID);
                            }
                            if (lineElements[i + 3].trim().matches(REGEX_FLAG)) {
                                fifoFlag = lineElements[i + 3].trim().equalsIgnoreCase("Y");
                            }  else {
                                throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.STATION_FLAG_INVALID);
                            }

                            ArrayList<Task> tasks = new ArrayList<>();
                            // use global here
                            taskParserIterator = i + 3;
                            while (lineElements.length > taskParserIterator + 1) {
                                taskParserIterator++;
                                // break loop if not job args
                                if (!lineElements[taskParserIterator].trim().matches(REGEX_TASK)
                                        && !lineElements[taskParserIterator].trim().matches(REGEX_FLOAT)) {
                                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASK_INVALID);
                                }
                                // look for task definitions
                                tasks.add(parseTask(lineElements, lineIndex, true));
                            }
                            // return to local
                            i = taskParserIterator;
                            taskParserIterator = -1;

                            if (!multiFlag) {
                                try {
                                    // grab jobtype for the first task
                                    JobType stationJobType = FindJobTypeFromTaskId(tasks.getFirst().getID());
                                    // check other task's jobtype
                                    if (tasks.size() > 1) {
                                        for (int j = 1; j < tasks.size(); j++) {
                                            JobType taskJobType = FindJobTypeFromTaskId(tasks.get(j).getID());
                                            assert stationJobType != null;
                                            assert taskJobType != null;
                                            // check id match
                                            System.out.printf("[%s] comparing %s to %s\n", stationId, stationJobType.Id, taskJobType.Id);
                                            if (!taskJobType.Id.equals(stationJobType.Id)) {
                                                throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.MULTIFLAG_FAIL);
                                            }
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.MULTIFLAG_FAIL);
                                }
                            }

                            parsedObjects.add(new Station(stationId, stationCapacity, multiFlag, fifoFlag, tasks));
                        }  else {
                            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.STATION_ID_INVALID);
                        }
                }
            }
        }
        return parsedObjects;
    }

    /**
     * Parses a task from string, starting from index
     */
    private static Task parseTask(String[] elements, int lineIndex, boolean checkPlusMinus) throws FileErrorException {
        if (elements[taskParserIterator].trim().matches(REGEX_TASK)) {
            // check if there is next element
            if (elements.length > taskParserIterator + 1) {
                // check if positive float
                String nextElement = elements[taskParserIterator + 1].trim();
                if (nextElement.matches(REGEX_FLOAT)) {
                    taskParserIterator++; // size found, skip iterator to next element

                    // look for plus minus
                    if (checkPlusMinus) {
                        if (elements.length > taskParserIterator + 1) {
                            String nextElement2 = elements[taskParserIterator + 1].trim();
                            if (nextElement2.matches(REGEX_FLOAT)) {
                                taskParserIterator++; // skip again
                                try {
                                    return new Task(findTaskTypeFromID(elements[taskParserIterator - 2]),
                                            Float.parseFloat(nextElement), Float.parseFloat(nextElement2));
                                } catch (TaskTypeNotFoundException e) {
                                    throw new FileErrorException(lineIndex,
                                            FileErrorException.ExceptionCause.TASKTYPE_NOT_DEFINED);
                                }
                            }
                        }
                    }

                    try {
                        return new Task(findTaskTypeFromID(elements[taskParserIterator - 1]),
                                Float.parseFloat(nextElement));
                    } catch (TaskTypeNotFoundException e) {
                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASKTYPE_NOT_DEFINED);
                    }
                } else {
                    if (!nextElement.matches(REGEX_SECTION) && !nextElement.matches(REGEX_TASK)
                            && !nextElement.matches(REGEX_JOB)) {
                        // throw error if not end of section, task or job
                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASK_SIZE_INVALID);
                    }
                }
            }
            // No size defined, construct default task
            try {
                return new Task(findTaskTypeFromID(elements[taskParserIterator]));
            } catch (TaskTypeNotFoundException e) {
                throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASKTYPE_NOT_DEFINED);
            }
        } else return null;
    }

    private static TaskType findTaskTypeFromID(String taskID) throws TaskTypeNotFoundException {
        for (TaskType taskType : taskTypes) {
            if (taskType.Id.equals(taskID)) {
                return taskType;
            }
        }
        throw new TaskTypeNotFoundException();
    }

    private static Job parseJobLine(final String jobString, final int lineIndex)
            throws FileErrorException {
        // Check line is not empty
        if (jobString.isEmpty()) {
            return null;
        }
        // Split the line in whitespaces
        String[] args = getJobArgs(jobString, lineIndex);

        String jobID = args[0];

        // Find matching id from types
        JobType jobType = null;
        String queryId = args[1]; // cache id
        for (JobType j : jobTypes) {
            if (j.Id.equals(queryId)) {
                jobType = j;
                break;
            }
        }
        if (jobType == null) {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.JOBTYPE_INVALID);
        }

        int startTime, duration;
        // Parse start time
        try {
            startTime = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.START_TIME_INVALID);
        }
        // Parse duration
        try {
            duration = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.DURATION_INVALID);
        }
        // Create and return job object
        return new Job(jobID, jobType, startTime, duration);
    }

    private static String[] getJobArgs(String jobString, int lineIndex) throws FileErrorException {
        String[] args = jobString.trim().split("\\s+");

        // Ensure there is 4 arguments
        if (args.length < 4) {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.ARGS_FEW);
        } else if (args.length > 4) {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.ARGS_MANY);
        }

        // check if id is unique
        for (Job job : jobs) {
            if (job.getJobID().equals(args[0])) {
                throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.JOB_INVALID);
            }
        }
        return args;
    }

    /** Prints all errors encountered in order */
    private static boolean reportErrors() {
        boolean bIsErrorFree = true;
        if (!jobFileErrors.isEmpty()) {
            bIsErrorFree = false;
            System.out.printf("Job file errors (%d)\n", jobFileErrors.size());
            for (FileErrorException e : jobFileErrors) {
                System.out.println("  " + e.getMessage());
            }
        }
        if (!workflowFileErrors.isEmpty()) {
            // leave a blank line if there were errors in job file
            if (!bIsErrorFree)
                System.out.println();
            bIsErrorFree = false;
            System.out.printf("Workflow file errors (%s)\n", workflowFileErrors.size());
            for (FileErrorException e : workflowFileErrors) {
                System.out.println("  " + e.getMessage());
            }
        }
        return bIsErrorFree;
    }

    public static Job FindJobFromTask(Task task) {
        if (jobs.isEmpty())
        {
            System.out.println("Jobs are not initialized yet.");
            return null;
        }

        for (Job job : jobs) {
            for (Task jobTask : job.getJobType().getTasks()) {
                if (jobTask.taskType.getTaskId().equals(task.taskType.Id)) {
                    return job;
                }
            }
        }
        return null;
    }

    public static JobType FindJobTypeFromTaskId(String taskID) {
        if (jobTypes.isEmpty())
        {
            System.out.println("jobsTypes are not initialized yet.");
            return null;
        }

        for (JobType jobType : jobTypes) {
            for (Task jobTask : jobType.getTasks()) {
                if (jobTask.taskType.getTaskId().equals(taskID)) {
                    return jobType;
                }
            }
        }
        return null;
    }
}