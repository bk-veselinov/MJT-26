package bg.sofia.uni.fmi.mjt.authserver.command.user;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidSessionIdException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;

public class UpdateUserDataCommand extends AuthenticatedUserCommand {

    private static final String SUCCESSFUL_COMMAND_MESSAGE =
            "Command executed successfully. Your user data was changed.";
    private static final String COMMAND_REGEX = "^\\s*--session-id\\s+\\S+(\\s+--new-username\\s+\\S+)?" +
            "(\\s+--new-first-name\\s+\\S+)?(\\s+--new-last-name\\s+\\S+)?(\\s+--new-email\\s+\\S+)?$";

    private String sessionId;
    private String newUsername;
    private String newFirstName;
    private String newLastName;
    private String newEmail;

    public UpdateUserDataCommand(String inputParameters) {
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
            authenticationManager.updateUserData(sessionId, newUsername, newFirstName, newLastName, newEmail);

        } catch (InvalidSessionIdException e) {
            connectionData.clearConnectionData();
            throw new InvalidSessionIdException(e.getMessage(), e);

        }

        return SUCCESSFUL_COMMAND_MESSAGE;
    }

    @Override
    protected void parseInputParameters() {
        sessionId = CommandParser.getParameter(inputParameters, "session-id");
        newUsername = CommandParser.getParameter(inputParameters, "new-username");
        newFirstName = CommandParser.getParameter(inputParameters, "new-first-name");
        newLastName = CommandParser.getParameter(inputParameters, "new-last-name");
        newEmail = CommandParser.getParameter(inputParameters, "new-email");
    }
}
