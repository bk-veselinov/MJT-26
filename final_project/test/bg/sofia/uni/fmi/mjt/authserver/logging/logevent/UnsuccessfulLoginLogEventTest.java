package bg.sofia.uni.fmi.mjt.authserver.logging.logevent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class UnsuccessfulLoginLogEventTest {

    @Test
    void testToLogDataContainsUserIP() {
        UnsuccessfulLoginLogEvent event = UnsuccessfulLoginLogEvent.newEventRecord("10.10.10.10");
        String logData = event.toLogData();
        assertTrue(logData.contains("10.10.10.10"), "toLogData should contain the user IP");
    }

    @Test
    void testToLogDataContainsEventType() {
        UnsuccessfulLoginLogEvent event = UnsuccessfulLoginLogEvent.newEventRecord("10.10.10.10");
        String logData = event.toLogData();
        assertTrue(logData.contains(LogEventType.FAILED_LOGIN.name()),
                "toLogData should contain the FAILED_LOGIN event type");
    }

    @Test
    void testToLogDataContainsDelimiter() {
        UnsuccessfulLoginLogEvent event = UnsuccessfulLoginLogEvent.newEventRecord("10.10.10.10");
        String logData = event.toLogData();
        assertTrue(logData.contains(" | "), "toLogData should contain the delimiter");
    }
}