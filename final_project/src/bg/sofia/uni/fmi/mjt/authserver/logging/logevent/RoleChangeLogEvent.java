package bg.sofia.uni.fmi.mjt.authserver.logging.logevent;

import java.util.UUID;

public class RoleChangeLogEvent extends LogEvent {

    private final String operationId;
    private String affectedUser;
    private RoleChange roleChange;
    private final String outcome;

    private static final String DELIMITER = " | ";
    private static final String SUCCESSFUL_OUTCOME = "Success";
    private static final String UNSUCCESSFUL_OUTCOME = "Failure";

    public RoleChangeLogEvent(String userIP, String affectedUser, RoleChange roleChange) {
        super(userIP, LogEventType.CONFIGURATION_CHANGE);
        this.operationId = UUID.randomUUID().toString();
        this.affectedUser = affectedUser;
        this.roleChange = roleChange;
        this.outcome = null;
    }

    private RoleChangeLogEvent(String operationId, String userIP, Boolean isSuccessful) {
        super(userIP, LogEventType.CONFIGURATION_CHANGE);
        this.operationId = operationId;
        this.outcome = isSuccessful ? SUCCESSFUL_OUTCOME : UNSUCCESSFUL_OUTCOME;

    }

    public static RoleChangeLogEvent newEventRecord(String userIP, String affectedUser, RoleChange roleChange) {
        return new RoleChangeLogEvent(userIP, affectedUser, roleChange);
    }

    public RoleChangeLogEvent recordEventEnd(Boolean isSuccessful) {
        return new RoleChangeLogEvent(operationId, userIP, isSuccessful);
    }

    @Override
    public String toLogData() {
        String specificData = outcome == null
                ? affectedUser + DELIMITER + roleChange.name()
                : outcome;

        return String.join(DELIMITER,
                timeStamp.toString(),
                operationId,
                logEventType.name(),
                userIP,
                specificData);
    }

}
