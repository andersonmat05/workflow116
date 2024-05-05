public class Main {
    public static void main(String[] args) {
        FileError fileError = new FileError(5, FileError.ErrorType.SYNTAX_ERROR, FileError.ErrorReason.UNKNOWN);
        System.out.println(fileError.getMessage());
    }
}
