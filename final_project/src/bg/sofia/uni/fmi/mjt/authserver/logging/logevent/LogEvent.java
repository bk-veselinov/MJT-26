package bg.sofia.uni.fmi.mjt.authserver.logging.logevent;

import java.time.LocalDateTime;

public abstract class LogEvent {

    protected LocalDateTime timeStamp;
    protected LogEventType logEventType;
    protected String userIP;

    public LogEvent(String userIP, LogEventType logEventType) {
        this.timeStamp = LocalDateTime.now();
        this.logEventType = logEventType;
        this.userIP = userIP;
    }

    public abstract String toLogData();

}
