public class FileErrorException extends Exception {
    private final int line;
    private final ExceptionCause cause;

    public FileErrorException(int line, ExceptionCause exceptionCause) {
        this.line = line;
        this.cause = exceptionCause;
    }

    /**
     * @return User friendly report message of the error.
     */
    @Override
    public String getMessage() {
        return String.format("%s at line %s: %s", cause.Type.text, line, cause.Message);
    }

    public enum ExceptionType {
        UNKNOWN("Unknown Error"),
        SYNTAX_ERROR("Syntax Error"),
        SEMANTIC_ERROR("Semantic Error");

        /** Display name of the error type. **/
        public final String text;

        ExceptionType(String text) {
            this.text = text;
        }
    }

    public enum ExceptionCause {
        UNKNOWN(ExceptionType.UNKNOWN, "An unknown error occurred."),
        LINE_BLANK(ExceptionType.SYNTAX_ERROR, "Line is blank."),
        ARGS_FEW(ExceptionType.SYNTAX_ERROR, "Too few arguments supplied."),
        ARGS_MANY(ExceptionType.SYNTAX_ERROR, "Too many arguments supplied."),
        // job
        START_TIME_INVALID(ExceptionType.SEMANTIC_ERROR, "Specified start time could not be parsed as a number."),
        DURATION_INVALID(ExceptionType.SEMANTIC_ERROR, "Specified duration could not be parsed as a number."),
        JOBTYPE_INVALID(ExceptionType.SEMANTIC_ERROR, "Specified job type is not defined in workflow file."),
        // end job

        // workflow
        SECTION_IDENTIFIER_INVALID(ExceptionType.SEMANTIC_ERROR, "Unknown section identifier."),
        TASKTYPES_NOT_FIRST(ExceptionType.SYNTAX_ERROR, "TASKTYPES must be the first section in workflow file."),
        TASK_INVALID(ExceptionType.SEMANTIC_ERROR, "Task type defined incorrectly."),
        TASK_SIZE_INVALID(ExceptionType.SEMANTIC_ERROR, "Task size is not a floating point number."),
        STATION_ID_INVALID(ExceptionType.SEMANTIC_ERROR, "Station Id is defined incorrectly."),
        STATION_CAPACITY_INVALID(ExceptionType.SEMANTIC_ERROR, "Station capacity is defined incorrectly."),
        STATION_FLAG_INVALID(ExceptionType.SEMANTIC_ERROR, "Flag is defined incorrectly."),
        JOB_INVALID(ExceptionType.SEMANTIC_ERROR, "Job type defined incorrectly."),
        TASKTYPE_NOT_DEFINED(ExceptionType.SEMANTIC_ERROR, "Task type not defined."),
        TASK_PLUSMINUS_INVALID(ExceptionType.SEMANTIC_ERROR, "Task' plusminus defined incorrectly"),
        // end workflow
        ;

        /** Type of the error. **/
        public final ExceptionType Type;
        /** Detailed description of the exception **/
        public final String Message;

        ExceptionCause(ExceptionType Name, String Message) {
            this.Type = Name;
            this.Message = Message;
        }
    }

}
