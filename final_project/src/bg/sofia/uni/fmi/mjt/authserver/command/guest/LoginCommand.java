package bg.sofia.uni.fmi.mjt.authserver.command.guest;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.command.Command;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.entity.Session;
import bg.sofia.uni.fmi.mjt.authserver.exception.BannedUserException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.LoginException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.logging.logevent.UnsuccessfulLoginLogEvent;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;

public class LoginCommand extends Command {

    private static final String SUCCESSFUL_COMMAND_MESSAGE = "Command executed successfully. Welcome back!";
    private static final String COMMAND_REGEX = "^\\s*((--username\\s+\\S+\\s+--password\\s+\\S+)" +
            "|(--session-id\\s+\\S+))$";

    private String sessionId;
    private String username;
    private String password;

    public LoginCommand(String inputParameters) {
        super(inputParameters);
        validateCommand(COMMAND_REGEX);
        parseInputParameters();
    }

    @Override
    public String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger) {
        ConnectionData connectionData = getConnectionData(key);

        if (connectionData.isBanned()) {
            throw new BannedUserException("User is banned from logging in.");
        }

        Session newSession = executeLogin(authenticationManager, connectionData, getUserIP(key), logger);
        connectionData.registerSuccessfulLogin(newSession.getId(), newSession.getUserId());

        return SUCCESSFUL_COMMAND_MESSAGE + " Session-id: " + newSession.getId();
    }

    @Override
    protected void parseInputParameters() {
        sessionId = CommandParser.getParameter(inputParameters, "session-id");
        username = CommandParser.getParameter(inputParameters, "username");
        password = CommandParser.getParameter(inputParameters, "password");
    }

    private Session executeLogin(AuthenticationManager manager,
                                 ConnectionData connectionData,
                                 String userIP,
                                 AuditLog logger) {
        Session newSession;
        try {
            if (inputParameters.contains("session-id")) {
                if (connectionData.isLogged()) {
                    throw new InvalidActionException("User is already logged in.");
                }
                newSession = manager.loginUserWithSessionId(this.sessionId);
            } else {
                newSession = manager.loginUser(username, password);
            }
        } catch (Exception e) {
            connectionData.registerUnsuccessfulLogin();
            logger.logEvent(UnsuccessfulLoginLogEvent.newEventRecord(userIP));
            throw new LoginException(e.getMessage(), e);

        }
        return newSession;
    }

}
