package bg.sofia.uni.fmi.mjt.authserver.exception;

public class InvalidSessionIdException extends RuntimeException {
    public InvalidSessionIdException(String message) {
        super(message);
    }

    public InvalidSessionIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
