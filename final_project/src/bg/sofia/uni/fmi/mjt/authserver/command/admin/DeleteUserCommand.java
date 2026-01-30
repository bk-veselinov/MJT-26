package bg.sofia.uni.fmi.mjt.authserver.command.admin;

import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InactiveSessionException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;

public class DeleteUserCommand extends AdminCommand {

    private static final String SUCCESSFUL_COMMAND_MESSAGE_BEGINNING =
            "Command executed successfully. User: ";
    private static final String SUCCESSFUL_COMMAND_MESSAGE_END = " was deleted.";

    public DeleteUserCommand(String inputParameters) {
        super(inputParameters);
    }

    @Override
    public String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger) {
        ConnectionData connectionData = getConnectionData(key);

        validateUserLoginStatus(connectionData);
        validateSessionId(connectionData, sessionId);

        try {
            authenticationManager.removeUser(sessionId, username);

        } catch (InactiveSessionException e) {
            connectionData.clearConnectionData();
            throw new InactiveSessionException(e.getMessage(), e);

        }

        return SUCCESSFUL_COMMAND_MESSAGE_BEGINNING + username + SUCCESSFUL_COMMAND_MESSAGE_END;
    }

}
