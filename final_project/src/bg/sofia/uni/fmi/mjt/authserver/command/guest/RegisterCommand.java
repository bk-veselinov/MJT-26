package bg.sofia.uni.fmi.mjt.authserver.command.guest;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.command.Command;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.entity.Session;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;

public class RegisterCommand extends Command {

    private static final String SUCCESSFUL_COMMAND_MESSAGE = "Command executed successfully. Hello, ";
    private static final String COMMAND_REGEX =
            "^\\s*--username\\s+\\S+\\s+--password\\s+\\S+\\s+" +
                    "--first-name\\s+\\S+\\s+--last-name\\s+\\S+\\s+--email\\s+\\S+$";

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    public RegisterCommand(String inputParameters) {
        super(inputParameters);
        validateCommand(COMMAND_REGEX);
        parseInputParameters();
    }

    @Override
    public String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger) {
        ConnectionData connectionData = getConnectionData(key);

        if (connectionData.isLogged()) {
            throw new InvalidActionException("User is already logged in.");
        }

        Session newSession = authenticationManager.registerUser(username, password, firstName, lastName, email);
        connectionData.registerSuccessfulLogin(newSession.getId(), newSession.getUserId());

        return SUCCESSFUL_COMMAND_MESSAGE + username + ". Session-id: " + newSession.getId();
    }

    @Override
    protected void parseInputParameters() {
        username = CommandParser.getParameter(inputParameters, "username");
        password = CommandParser.getParameter(inputParameters, "password");
        firstName = CommandParser.getParameter(inputParameters, "first-name");
        lastName = CommandParser.getParameter(inputParameters, "last-name");
        email = CommandParser.getParameter(inputParameters, "email");
    }
}
