package bg.sofia.uni.fmi.mjt.authserver.logging.logevent;

public class UnsuccessfulLoginLogEvent extends LogEvent {

    private static final String DELIMITER = " | ";

    public UnsuccessfulLoginLogEvent(String userIP) {
        super(userIP, LogEventType.FAILED_LOGIN);
    }

    public static UnsuccessfulLoginLogEvent newEventRecord(String userIP) {
        return new UnsuccessfulLoginLogEvent(userIP);
    }

    @Override
    public String toLogData() {
        return String.join(DELIMITER, timeStamp.toString(), logEventType.name(), userIP);
    }
}
