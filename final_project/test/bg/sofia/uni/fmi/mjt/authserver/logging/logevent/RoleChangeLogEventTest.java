package bg.sofia.uni.fmi.mjt.authserver.logging.logevent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleChangeLogEventTest {

    @Test
    void testToLogDataContainsUserIP() {
        RoleChangeLogEvent event = RoleChangeLogEvent.newEventRecord(
                "10.10.10.10", "targetUser", RoleChange.ADD_ADMIN_ROLE);
        String logData = event.toLogData();
        assertTrue(logData.contains("10.10.10.10"), "toLogData should contain the user IP");
    }

    @Test
    void testToLogDataContainsAffectedUser() {
        RoleChangeLogEvent event = RoleChangeLogEvent.newEventRecord(
                "10.10.10.10", "targetUser", RoleChange.REMOVE_ADMIN_ROLE);
        String logData = event.toLogData();
        assertTrue(logData.contains("targetUser"), "toLogData should contain the affected username");
    }

    @Test
    void testToLogDataContainsRoleChangeType() {
        RoleChangeLogEvent event = RoleChangeLogEvent.newEventRecord(
                "10.10.10.10", "user", RoleChange.ADD_ADMIN_ROLE);
        String logData = event.toLogData();
        assertTrue(logData.contains(RoleChange.ADD_ADMIN_ROLE.name()),
                "toLogData should contain the role change type");
    }

    @Test
    void testToLogDataContainsConfigurationChangeEventType() {
        RoleChangeLogEvent event = RoleChangeLogEvent.newEventRecord(
                "10.10.10.10", "user", RoleChange.ADD_ADMIN_ROLE);
        String logData = event.toLogData();
        assertTrue(logData.contains(LogEventType.CONFIGURATION_CHANGE.name()),
                "toLogData should contain CONFIGURATION_CHANGE event type");
    }

    @Test
    void testRecordEventEndSuccessContainsSuccess() {
        RoleChangeLogEvent event = RoleChangeLogEvent.newEventRecord(
                "10.10.10.10", "user", RoleChange.ADD_ADMIN_ROLE);
        RoleChangeLogEvent endEvent = event.recordEventEnd(true);
        String logData = endEvent.toLogData();
        assertTrue(logData.contains("Success"), "toLogData for successful end event should contain 'Success'");
    }

    @Test
    void testRecordEventEndFailureContainsFailure() {
        RoleChangeLogEvent event = RoleChangeLogEvent.newEventRecord(
                "10.10.10.10", "user", RoleChange.REMOVE_ADMIN_ROLE);
        RoleChangeLogEvent endEvent = event.recordEventEnd(false);
        String logData = endEvent.toLogData();
        assertTrue(logData.contains("Failure"), "toLogData for failed end event should contain 'Failure'");
    }

    @Test
    void testToLogDataContainsDelimiter() {
        RoleChangeLogEvent event = RoleChangeLogEvent.newEventRecord(
                "10.10.10.10", "user", RoleChange.ADD_ADMIN_ROLE);
        String logData = event.toLogData();
        assertTrue(logData.contains(" | "), "toLogData should contain the delimiter");
    }
}
