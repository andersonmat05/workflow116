import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileManager {
    public static final String REGEX_SECTION = "[A-Z]+";
    public static final String REGEX_OBJECT = "^[A-Z](_)?[0-9]+$";
    public static final String REGEX_FLOAT = "([0-9]*[.])?[0-9]+";

    private static final ArrayList<FileErrorException> jobFileErrors = new ArrayList<>();
    private static final ArrayList<FileErrorException> workflowFileErrors = new ArrayList<>();

    private enum WorkflowSection {
        INVALID_SECTION,
        TASKTYPES,
        JOBTYPES,
        STATIONS;
    }

    public static void parseFiles(String workflowFile, String jobFile) {

        //todo: Parse workflow file
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
            try {
                Task task = (Task) parsedObject;
                System.out.println(String.format("[TASK] %s %s", task.type, task.size));
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        return stations;
    }

    /**
     * @param section Last used section type
     * @param lineElements Array of elements in the line to be parsed
     * @param lineIndex Used for error reporting
     */
    private static void parseWorkflowLine(ArrayList<Object> objects, WorkflowSection section, String[] lineElements, int lineIndex) throws FileErrorException {
        System.out.println("Parsing line: " + lineIndex + " -> " + Arrays.toString(lineElements));

        // iterate elements
        for (int i = 0; i < lineElements.length; i++) {
            // assign section
            if (lineElements[i].trim().matches(REGEX_SECTION)) {
                System.out.println("IDENTIFIER ELEMENT: " + lineElements[i]);
                try {
                    section = WorkflowSection.valueOf(lineElements[i]);
                } catch (IllegalArgumentException e) {
                    throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.SECTION_IDENTIFIER_INVALID);
                }
            } else {
                // ensure section
                if (section == WorkflowSection.INVALID_SECTION) {
                    System.out.println("Invalid section at line: " + lineIndex);
                    // something gone wrong, abort func
                    return;
                }

                switch (section) {
                    case TASKTYPES:
                        // ensure syntax
                        if (lineElements[i].trim().matches(REGEX_OBJECT)) {
                            System.out.println("TASKTYPE ELEMENT: " + lineElements[i]);

                            // check if there is next element
                            if (lineElements.length-1 > i+1) {
                                // check if positive float
                                if (lineElements[i+1].trim().matches(REGEX_FLOAT)) {
                                    objects.add(new Task(lineElements[i].trim(), Float.parseFloat(lineElements[i+1])));
                                    i++; // skip to next element
                                    continue;
                                }
                            }
                            objects.add(new Task(lineElements[i]));
                        } else {
                            throw new FileErrorException(lineIndex, FileErrorException.ExceptionCause.TASK_INVALID);
                        }
                        break;
                    case JOBTYPES:
                        //TODO
                        break;
                    case STATIONS:
                        //TODO
                        break;
                }
            }
        }
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
        //todo: check if unique
        String jobID = args[0];
        //todo: check workflow to find definition
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
