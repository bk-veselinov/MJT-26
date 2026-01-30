package bg.sofia.uni.fmi.mjt.authserver.exception;

public class InactiveSessionException extends RuntimeException {
    public InactiveSessionException(String message) {
        super(message);
    }

    public InactiveSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
