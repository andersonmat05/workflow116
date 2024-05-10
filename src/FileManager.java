import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileManager {
    public static final String REGEX_SECTION =  "[A-Z]+";
    public static final String REGEX_TASK =     "^(T)(_)?[0-9]+$";
    public static final String REGEX_JOB =      "^(J)(_)?[0-9]+$";
    public static final String REGEX_STATION =  "^(S)(_)?[0-9]+$";
    public static final String REGEX_FLAG =     "^(?:Y|N)$";
    public static final String REGEX_FLOAT =    "^([0-9]*[.])?[0-9]+$";

    private static final ArrayList<FileErrorException> jobFileErrors = new ArrayList<>();
    private static final ArrayList<FileErrorException> workflowFileErrors = new ArrayList<>();

    private static ArrayList<TaskType> taskTypes = new ArrayList<>();
    private static ArrayList<Station> stations = new ArrayList<>();

    // global types because fuck java
    private static WorkflowSection section;
    private static int taskParserIterator;

    private enum WorkflowSection {
        INVALID_SECTION,
        TASKTYPES,
        JOBTYPES,
        STATIONS
    }

    public static ArrayList<Station> getStations() {
        return stations;
    }

    private static class TaskTypeNotFoundException extends RuntimeException {
    }

    public static void parseFiles(String workflowFile, String jobFile) {

        ArrayList<JobType> jobTypes = new ArrayList<>();
        try {
            parseWorkflowFile(workflowFile, jobTypes);
        } catch (FileNotFoundException e) {
            System.out.println("Error reading workflow file: " + e.getMessage());
        }

        // Parse job file
        try {
            ArrayList<Job> jobs = parseJobFile(jobFile, jobTypes);
            for (Job job : jobs) {
                //job.printInfo();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading job file: " + e.getMessage());
        }

        // Report all errors
        reportErrors();
    }

    /**
     * Parses each line of the job file and return created job objects
     **/
    private static ArrayList<Job> parseJobFile(String jobFile, ArrayList<JobType> jobTypes) throws FileNotFoundException {
        ArrayList<Job> jobs = new ArrayList<>();
        Scanner sc = new Scanner(new File(jobFile));
        int lineIndex = 0;
        while (sc.hasNextLine()) {
            try {
                Job newJob = parseJobLine(sc.nextLine(), jobTypes, ++lineIndex);
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
    private static void parseWorkflowFile(String jobFile, ArrayList<JobType> jobTypes) throws FileNotFoundException {
        ArrayList<Station> stations = new ArrayList<>();
        // file not found thrown here
        Scanner sc = new Scanner(new File(jobFile));
        int lineIndex = 0;

        // objects extracted from the file
        ArrayList<Object> parsedObjects = new ArrayList<>();
        section = WorkflowSection.INVALID_SECTION;

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
                parseWorkflowLine(parsedObjects, lineElements, lineIndex);

            } catch (FileErrorException e) {
                workflowFileErrors.add(e);
            }
        }
        sc.close();

        for (Object parsedObject : parsedObjects) {
            if (parsedObject instanceof TaskType taskType) {
                System.out.println("TaskType object must NOT be returned from parse func.");
            }
            if (parsedObject instanceof JobType) {
                jobTypes.add((JobType) parsedObject);
            }
            if (parsedObject instanceof Station station) {
                //System.out.printf("Station: %s, Tasks: %s%n", station.getStationID(), station.getTasks().toString());
                stations.add(station);
            }
        }
        System.out.println();
        for (TaskType taskType : taskTypes) {
            //System.out.printf("TaskType: %s, size: %s%n", taskType.type, taskType.defaultSize);
        }

        FileManager.stations = stations;
    }

    /**
     * @param lineElements Array of elements in the line to be parsed
     * @param lineIndex    Used for error reporting
     */
    private static void parseWorkflowLine(ArrayList<Object> objects, String[] lineElements,
            int lineIndex) throws FileErrorException {
        // =System.out.println("Parsing line: " + lineIndex + " -> " +
        // Arrays.toString(lineElements));

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
                    if (section == WorkflowSection.INVALID_SECTION && newSection != WorkflowSection.TASKTYPES) {
                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASKTYPES_NOT_FIRST);
                    }

                    // Assign new section
                    section = newSection;
                } catch (IllegalArgumentException e) {
                    // enum conversion failed
                    throw new FileErrorException(lineIndex,
                            FileErrorException.ExceptionCause.SECTION_IDENTIFIER_INVALID);
                }
            } else {
                // ensure current section
                if (section == WorkflowSection.INVALID_SECTION) {
                    // something gone wrong, abort func
                    return;
                }

                switch (section) {
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

                            objects.add(new JobType(jobId, tasks));
                        } else {
                            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.JOB_INVALID);
                        }
                        break;
                    case STATIONS:
                        if (lineElements[i].trim().matches(REGEX_STATION)) {
                            //todo: Check arg types
                            if (lineElements.length < 5) {
                                throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.ARGS_FEW);
                            }

                            String stationId = lineElements[i].trim();
                            int stationCapacity = -1;
                            boolean multiFlag = false;
                            boolean fifoFlag = false;

                            try {
                                stationCapacity = Integer.parseInt(lineElements[i + 1].trim());
                            } catch (NumberFormatException e) {
                                throw new FileErrorException(lineIndex,
                                        FileErrorException.ExceptionCause.STATION_CAPACITY_INVALID);
                            }
                            if (lineElements[i + 2].trim().matches(REGEX_FLAG)) {
                                multiFlag = lineElements[i + 2].trim().equalsIgnoreCase("Y");
                            }
                            if (lineElements[i + 3].trim().matches(REGEX_FLAG)) {
                                fifoFlag = lineElements[i + 3].trim().equalsIgnoreCase("Y");
                            }

                            ArrayList<Task> tasks = new ArrayList<>();
                            // use global here
                            taskParserIterator = i + 3;
                            while (lineElements.length > taskParserIterator + 1) {
                                taskParserIterator++;
                                // break loop if not job args
                                if (!lineElements[taskParserIterator].trim().matches(REGEX_TASK)
                                        && !lineElements[taskParserIterator].trim().matches(REGEX_FLOAT)) {
                                    break;
                                }
                                // look for task definitions
                                tasks.add(parseTask(lineElements, lineIndex, true));
                            }
                            // return to local
                            i = taskParserIterator;
                            taskParserIterator = -1;

                            objects.add(new Station(stationId, stationCapacity, multiFlag, fifoFlag, tasks));
                        }
                        break;
                }
            }
        }
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
        } else {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASK_INVALID);
        }
    }

    private static TaskType findTaskTypeFromID(String taskID) throws TaskTypeNotFoundException {
        for (TaskType taskType : taskTypes) {
            if (taskType.type.equals(taskID)) {
                return taskType;
            }
        }
        throw new TaskTypeNotFoundException();
    }

    private static Job parseJobLine(final String jobString, ArrayList<JobType> jobTypes, final int lineIndex) throws FileErrorException {
        // Check line is not empty
        if (jobString.isEmpty()) {
            return null;
        }
        // Split the line in whitespaces
        String[] args = jobString.trim().split("\\s+");

        // Ensure there is 4 arguments
        if (args.length < 4) {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.ARGS_FEW);
        } else if (args.length > 4) {
            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.ARGS_MANY);
        }
        // todo: check if unique
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

    /** Prints all errors encountered in order. **/
    private static void reportErrors() {
        if (!jobFileErrors.isEmpty()) {
            System.out.println("Job file errors:");
            for (FileErrorException e : jobFileErrors) {
                System.out.println(e.getMessage());
            }
        }
        if (!workflowFileErrors.isEmpty()) {
            System.out.println("Workflow file errors:");
            for (FileErrorException e : workflowFileErrors) {
                System.out.println(e.getMessage());
            }
        }
    }
}