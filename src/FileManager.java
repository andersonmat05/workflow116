import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileManager {
    public static final String REGEX_SECTION =  "[A-Z]+";
    public static final String REGEX_TASK =     "^(T)(_)?[0-9]+$";
    public static final String REGEX_JOB =      "^(J)(_)?[0-9]+$";
    public static final String REGEX_STATION =  "^(S)(_)?[0-9]+$";
    public static final String REGEX_FLAG =     "^(?:Y|N)$";
    public static final String REGEX_FLOAT =    "([0-9]*[.])?[0-9]+";

    private static final ArrayList<FileErrorException> jobFileErrors = new ArrayList<>();
    private static final ArrayList<FileErrorException> workflowFileErrors = new ArrayList<>();

    private static ArrayList<TaskType> taskTypes = new ArrayList<>();

    private enum WorkflowSection {
        INVALID_SECTION, // initial section
        TASKTYPES,
        JOBTYPES,
        STATIONS;
    }

    private static class TaskTypeNotFoundException extends RuntimeException {
    }

    public static void parseFiles(String workflowFile, String jobFile) {

        // todo: Parse workflow file
        try {
            parseWorkflowFile(workflowFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error reading workflow file: " + e.getMessage());
        }

        // Parse job file
        try {
            ArrayList<Job> jobs = parseJobFile(jobFile);
            for (Job job : jobs) {
                job.printInfo();
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
    private static ArrayList<Job> parseJobFile(String jobFile) throws FileNotFoundException {
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
    private static ArrayList<Station> parseWorkflowFile(String jobFile) throws FileNotFoundException {
        ArrayList<Station> stations = new ArrayList<>();
        // file not found thrown here
        Scanner sc = new Scanner(new File(jobFile));
        int lineIndex = 0;

        WorkflowSection currentSection = WorkflowSection.INVALID_SECTION;
        // objects extracted from the file
        ArrayList<Object> parsedObjects = new ArrayList<>();

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
                parseWorkflowLine(parsedObjects, currentSection, lineElements, lineIndex);

            } catch (FileErrorException e) {
                workflowFileErrors.add(e);
            }
        }
        sc.close();

        for (Object parsedObject : parsedObjects) {
            if (parsedObject instanceof TaskType) {
                TaskType taskType = (TaskType) parsedObject;
                System.out.println("TaskType object must NOT be returned from parse func.");
                System.out.printf("[TASKTYPE] %s %s%n", taskType.type, taskType.defaultSize);
            }
            if (parsedObject instanceof Task) {

            }
            if (parsedObject instanceof Job) {

            }
            if (parsedObject instanceof Station) {

            }
        }
        for (TaskType taskType : taskTypes) {
            System.out.printf("TaskType: %s, size: %s%n", taskType.type, taskType.defaultSize);
        }

        return stations;
    }

    /**
     * @param section      Last used section type
     * @param lineElements Array of elements in the line to be parsed
     * @param lineIndex    Used for error reporting
     */
    private static void parseWorkflowLine(ArrayList<Object> objects, WorkflowSection section, String[] lineElements,
            int lineIndex) throws FileErrorException {
        System.out.println("Parsing line: " + lineIndex + " -> " + Arrays.toString(lineElements));

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
                    System.out.println("NEW SECTION: " + lineElements[i]);
                    section = newSection;
                } catch (IllegalArgumentException e) {
                    throw new FileErrorException(lineIndex,
                            FileErrorException.ExceptionCause.SECTION_IDENTIFIER_INVALID);
                }
            } else {
                // ensure current section
                if (section == WorkflowSection.INVALID_SECTION) {
                    System.out.println("Invalid section at line: " + lineIndex);
                    // something gone wrong, abort func
                    return;
                }

                switch (section) {
                    case TASKTYPES:
                        // ensure syntax
                        if (lineElements[i].trim().matches(REGEX_TASK)) {
                            // check if there is next element
                            if (lineElements.length > i+1) {
                                // check if positive float
                                String nextElement = lineElements[i+1].trim();
                                if (nextElement.matches(REGEX_FLOAT)) {
                                    // add to types
                                    taskTypes.add(new TaskType(lineElements[i].trim(), Float.parseFloat(nextElement)));
                                    i++; // skip to next element
                                    continue;
                                } else {
                                    if (!nextElement.matches(REGEX_SECTION) && !nextElement.matches(REGEX_TASK)) {
                                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASK_SIZE_INVALID);
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
                            ArrayList<Task> tasks = new ArrayList<>();
                            // populate task array
                            while (lineElements.length > i+1) {
                                // break loop if not job args
                                if (!lineElements[i+1].trim().matches(REGEX_TASK) && !lineElements[i+1].trim().matches(REGEX_FLOAT)) {
                                    break;
                                }
                                // look for task definitions
                                parseTask(lineElements, i, lineIndex);
                            }
                        } else {
                            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.JOB_INVALID);
                        }
                        break;
                    case STATIONS:
                        // TODO
                        break;
                }
            }
        }
    }


    /**
     * Parses a task from string, starting from index
     */
    private static Task parseTask(String[] elements, Integer index, int lineIndex) throws FileErrorException {
        if (elements[index].trim().matches(REGEX_TASK)) {
            // check if there is next element
            if (elements.length > index+1) {
                // check if positive float
                String nextElement = elements[index+1].trim();
                if (nextElement.matches(REGEX_FLOAT)) {
                    index++; // skip to next element
                    try {
                        return new Task(findTaskTypeFromID(elements[index]), Float.parseFloat(nextElement));
                    } catch (TaskTypeNotFoundException e) {
                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASKTYPE_NOT_DEFINED);
                    }
                } else {
                    if (!nextElement.matches(REGEX_SECTION) && !nextElement.matches(REGEX_TASK) && !nextElement.matches(REGEX_JOB)) {
                        // throw error if not end of section, task or job
                        throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASK_SIZE_INVALID);
                    }
                }
            }
            // No size defined, construct default task
            try {
                return new Task(findTaskTypeFromID(elements[index]));
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

    private static Job parseJobLine(final String jobString, final int line) throws FileErrorException {
        // Check line is not empty
        if (jobString.isEmpty()) {
            return null;
        }
        // Split the line in whitespaces
        String[] args = jobString.trim().split("\\s+");

        // Ensure there is 4 arguments
        if (args.length < 4) {
            throw new FileErrorException(line, FileErrorException.ExceptionCause.ARGS_FEW);
        } else if (args.length > 4) {
            throw new FileErrorException(line, FileErrorException.ExceptionCause.ARGS_MANY);
        }
        // todo: check if unique
        String jobID = args[0];
        // todo: check workflow to find definition
        String jobTypeID = args[1];

        int startTime, duration;
        // Parse start time
        try {
            startTime = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new FileErrorException(line, FileErrorException.ExceptionCause.START_TIME_INVALID);
        }
        // Parse duration
        try {
            duration = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new FileErrorException(line, FileErrorException.ExceptionCause.DURATION_INVALID);
        }
        // Create and return job object
        return new Job(jobID, jobTypeID, startTime, duration);
    }

    /** Prints all errors encountered in order. **/
    private static void reportErrors() {
        if (!jobFileErrors.isEmpty()) {
            for (FileErrorException e : jobFileErrors) {
                System.out.println(e.getMessage());
            }
        }
        if (!workflowFileErrors.isEmpty()) {
            for (FileErrorException e : workflowFileErrors) {
                System.out.println(e.getMessage());
            }
        }
    }
}