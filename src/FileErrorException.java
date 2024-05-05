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

        private ExceptionType(String text) {
            this.text = text;
        }
    }

    public enum ExceptionCause {
        UNKNOWN(ExceptionType.UNKNOWN, "an unknown error occurred."),
        LINE_BLANK(ExceptionType.SYNTAX_ERROR , "Line is blank."),
        ARGS_FEW(ExceptionType.SYNTAX_ERROR,"Too few arguments supplied."),
        ARGS_MANY(ExceptionType.SYNTAX_ERROR, "Too many arguments supplied."),
        // job
        START_TIME_INVALID(ExceptionType.SYNTAX_ERROR, "Specified start time could not be parsed as a number."),
        DURATION_INVALID(ExceptionType.SYNTAX_ERROR, "Specified duration could not be parsed as a number."),
        // end job
        ;
        /** Type of the error. **/
        public final ExceptionType Type;
        /** Detailed description of the exception **/
        public final String Message;

        private ExceptionCause(ExceptionType Name, String Message) {
            this.Type = Name;
            this.Message = Message;
        }
    }

}
