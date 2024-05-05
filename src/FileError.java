public class FileError {
    private final int errorLine;
    private final ErrorType errorType;
    private final ErrorReason errorReason;

    public FileError(int errorLine, ErrorType errorType, ErrorReason errorReason) {
        this.errorLine = errorLine;
        this.errorType = errorType;
        this.errorReason = errorReason;
    }

    public String getMessage() {
        return String.format("%s: %s occurred at line %s, %s", errorType.Name, errorReason.Name, errorLine, errorReason.Message);
    }

    public enum ErrorType {
        UNKNOWN("Unknown"),
        SYNTAX_ERROR("Syntax Error"),
        SEMANTIC_ERROR("Semantic Error");

        /** Display name of the error type **/
        public final String Name;

        private ErrorType(String Name) {
            this.Name = Name;
        }
    }

    public enum ErrorReason {
        UNKNOWN("Unknown Error", "message"),
        FILE_ERROR_REASON1("File Error 1" , "Message1"),
        FILE_ERROR_REASON2("File error 2","Message2");

        /** Display name of the error **/
        public final String Name;
        /** Detailed description of the error **/
        public final String Message;

        private ErrorReason(String Name, String Message) {
            this.Name = Name;
            this.Message = Message;
        }
    }

}
