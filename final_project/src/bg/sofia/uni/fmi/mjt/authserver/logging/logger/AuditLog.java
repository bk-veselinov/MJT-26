package bg.sofia.uni.fmi.mjt.authserver.logging.logger;

import bg.sofia.uni.fmi.mjt.authserver.logging.logevent.LogEvent;

public interface AuditLog {
    void logEvent(LogEvent logEvent);
}
