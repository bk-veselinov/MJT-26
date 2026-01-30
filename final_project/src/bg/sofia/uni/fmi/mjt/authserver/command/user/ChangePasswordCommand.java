package bg.sofia.uni.fmi.mjt.authserver.command.user;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InactiveSessionException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;

public class ChangePasswordCommand extends AuthenticatedUserCommand {

    private static final String SUCCESSFUL_COMMAND_MESSAGE = "Command executed successfully. Password was changed.";
    private static final String COMMAND_REGEX = "^\\s*--session-id\\s+\\S+\\s+--username\\s+\\S+\\s+" +
            "--old-password\\s+\\S+\\s+--new-password\\s+\\S+$";

    private String sessionId;
    private String username;
    private String oldPassword;
    private String newPassword;

    public ChangePasswordCommand(String inputParameters) {
        super(inputParameters);
        validateCommand(COMMAND_REGEX);
        parseInputParameters();
    }

    @Override
    public String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger) {
        ConnectionData connectionData = getConnectionData(key);

        validateUserLoginStatus(connectionData);
        validateSessionId(connectionData, sessionId);

        try {
            authenticationManager.changePassword(sessionId, username, oldPassword, newPassword);

        } catch (InactiveSessionException e) {
            connectionData.clearConnectionData();
            throw new InactiveSessionException(e.getMessage(), e);

        }

        return SUCCESSFUL_COMMAND_MESSAGE;
    }

    @Override
    protected void parseInputParameters() {
        sessionId = CommandParser.getParameter(inputParameters, "session-id");
        username = CommandParser.getParameter(inputParameters, "username");
        oldPassword = CommandParser.getParameter(inputParameters, "old-password");
        newPassword = CommandParser.getParameter(inputParameters, "new-password");

    }
}
