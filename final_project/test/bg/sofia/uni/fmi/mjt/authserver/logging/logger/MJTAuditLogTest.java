package bg.sofia.uni.fmi.mjt.authserver.logging.logger;

import bg.sofia.uni.fmi.mjt.authserver.logging.logevent.LogEvent;
import bg.sofia.uni.fmi.mjt.authserver.util.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MJTAuditLogTest {

    @Mock
    private FileHandler fileHandler;

    @Mock
    private LogEvent logEvent;

    private MJTAuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new MJTAuditLog(fileHandler);
    }

    @Test
    void testLogEventWritesLogDataToFile() {
        when(logEvent.toLogData()).thenReturn("log data");

        auditLog.logEvent(logEvent);

        verify(fileHandler).writeData("log data\n", true);
    }

    @Test
    void testLogEventCallsToLogDataOnEvent() {
        when(logEvent.toLogData()).thenReturn("data");

        auditLog.logEvent(logEvent);

        verify(logEvent).toLogData();
    }
}