package bg.sofia.uni.fmi.mjt.authserver.logging.logger;

import bg.sofia.uni.fmi.mjt.authserver.util.FileHandler;
import bg.sofia.uni.fmi.mjt.authserver.logging.logevent.LogEvent;

public class MJTAuditLog implements AuditLog {

    private final FileHandler fileHandler;

    public MJTAuditLog(String fileName) {
        this.fileHandler = new FileHandler(fileName);
    }

    MJTAuditLog(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    @Override
    public void logEvent(LogEvent logEvent) {
        fileHandler.writeData(logEvent.toLogData() + "\n", true);
    }

}
