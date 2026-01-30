package bg.sofia.uni.fmi.mjt.authserver.command.user;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;

public class LogoutCommand extends AuthenticatedUserCommand {
    private static final String SUCCESSFUL_COMMAND_MESSAGE =
            "Command executed successfully. You are no longer logged in.";
    private static final String COMMAND_REGEX = "^\\s*--session-id\\s+\\S+$";

    private String sessionId;

    public LogoutCommand(String inputParameters) {
        super(inputParameters);
        validateCommand(COMMAND_REGEX);
        parseInputParameters();
    }

    @Override
    public String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger) {
        ConnectionData connectionData = getConnectionData(key);

        validateUserLoginStatus(connectionData);
        validateSessionId(connectionData, sessionId);

        authenticationManager.logoutUser(sessionId);
        connectionData.clearConnectionData();

        return SUCCESSFUL_COMMAND_MESSAGE;
    }

    @Override
    protected void parseInputParameters() {
        sessionId = CommandParser.getParameter(inputParameters, "session-id");
    }
}
