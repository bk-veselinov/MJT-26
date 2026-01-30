package bg.sofia.uni.fmi.mjt.authserver.command.admin;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.command.user.AuthenticatedUserCommand;

public abstract class AdminCommand extends AuthenticatedUserCommand {

    private static final String COMMAND_REGEX = "^\\s*--session-id\\s+\\S+\\s+--username\\s+\\S+$";
    protected String sessionId;
    protected String username;

    public AdminCommand(String inputParameters) {
        super(inputParameters);
        validateCommand(COMMAND_REGEX);
        parseInputParameters();
    }

    @Override
    protected void parseInputParameters() {
        sessionId = CommandParser.getParameter(inputParameters, "session-id");
        username = CommandParser.getParameter(inputParameters, "username");
    }
}
