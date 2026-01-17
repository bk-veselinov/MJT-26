package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class SearchParametersException extends RuntimeException {
    public SearchParametersException(String message) {
        super(message);
    }

    public SearchParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
