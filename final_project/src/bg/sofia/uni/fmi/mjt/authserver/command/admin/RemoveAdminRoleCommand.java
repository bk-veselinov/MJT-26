package bg.sofia.uni.fmi.mjt.authserver.command.admin;

import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InactiveSessionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.logging.logevent.RoleChange;
import bg.sofia.uni.fmi.mjt.authserver.logging.logevent.RoleChangeLogEvent;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;

public class RemoveAdminRoleCommand extends AdminCommand {

    private static final String SUCCESSFUL_COMMAND_MESSAGE_BEGINNING =
            "Command executed successfully. User: ";
    private static final String SUCCESSFUL_COMMAND_MESSAGE_END = " does not have admin role.";

    public RemoveAdminRoleCommand(String inputParameters) {
        super(inputParameters);
    }

    @Override
    public String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger) {
        RoleChangeLogEvent logEvent = RoleChangeLogEvent.newEventRecord(
                getUserIP(key), username, RoleChange.REMOVE_ADMIN_ROLE);
        logger.logEvent(logEvent);

        ConnectionData connectionData = getConnectionData(key);

        try {
            validateUserLoginStatus(connectionData);
            validateSessionId(connectionData, sessionId);

            authenticationManager.removeAdminRole(sessionId, username);
            logger.logEvent(logEvent.recordEventEnd(true));

            return SUCCESSFUL_COMMAND_MESSAGE_BEGINNING + username + SUCCESSFUL_COMMAND_MESSAGE_END;

        } catch (InactiveSessionException e) {
            connectionData.clearConnectionData();
            throw new InactiveSessionException(e.getMessage(), e);

        } catch (Exception e) {
            logger.logEvent(logEvent.recordEventEnd(false));
            throw new InvalidActionException(e.getMessage(), e);

        }
    }

}
