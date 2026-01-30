package bg.sofia.uni.fmi.mjt.authserver.exception;

public class BannedUserException extends RuntimeException {
    public BannedUserException(String message) {
        super(message);
    }

    public BannedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
