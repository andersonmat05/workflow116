package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
    private static ArrayList<FileErrorException> jobFileErrors = new ArrayList<>();
    private static ArrayList<FileErrorException> workflowFileErrors = new ArrayList<>();

    public static void parseFiles(String workflowFile, String jobFile) {
        try {
            ArrayList<Job> jobs = parseJobFile(jobFile);
            for (Job job : jobs) {
                job.printInfo();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading job file: " + e.getMessage());
        }

        reportErrors();
    }

    private static ArrayList<Job> parseJobFile(String jobFile) throws FileNotFoundException {
        ArrayList<Job> jobs = new ArrayList<>();
        Scanner sc = new Scanner(new File(jobFile));
        int index = 0;
        while (sc.hasNextLine()) {
            try {
                jobs.add(parseJobLine(sc.nextLine(), ++index));
            } catch (FileErrorException e) {
                jobFileErrors.add(e);
            }
        }
        return jobs;
    }

    private static Job parseJobLine(final String jobString, final int line) throws FileErrorException {
        // Check line is not empty
        if (jobString.isEmpty()) {
            throw new FileErrorException(line, FileErrorException.ExceptionCause.LINE_BLANK);
        }
        // Split the line in whitespaces
        String[] args = jobString.split("\\s+");

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
        try {
            startTime = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new FileErrorException(line, FileErrorException.ExceptionCause.START_TIME_INVALID);
        }
        try {
            duration = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new FileErrorException(line, FileErrorException.ExceptionCause.DURATION_INVALID);
        }
        return new Job(jobID, jobTypeID, startTime, duration);
    }

    private static void reportErrors() {
        if (jobFileErrors.size() > 0) {
            for (FileErrorException e : jobFileErrors) {
                System.out.println(e.getMessage());
            }
        }
        if (workflowFileErrors.size() > 0) {
            for (FileErrorException e : workflowFileErrors) {
                System.out.println(e.getMessage());
            }
        }
    }
}
