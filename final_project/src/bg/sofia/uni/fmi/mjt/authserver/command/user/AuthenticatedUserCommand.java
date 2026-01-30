package bg.sofia.uni.fmi.mjt.authserver.command.user;

import bg.sofia.uni.fmi.mjt.authserver.command.Command;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidSessionIdException;
import bg.sofia.uni.fmi.mjt.authserver.exception.UnauthenticatedUserException;

public abstract class AuthenticatedUserCommand extends Command {

    public AuthenticatedUserCommand(String inputParameters) {
        super(inputParameters);
    }

    protected void validateUserLoginStatus(ConnectionData connectionData) {
        if (!connectionData.isLogged()) {
            throw new UnauthenticatedUserException("The user is not logged in.");
        }
    }

    protected void validateSessionId(ConnectionData connectionData, String sessionId) {
        if (!connectionData.getSessionId().equals(sessionId)) {
            throw new InvalidSessionIdException(
                    "The used session id does not match the one given on login.");
        }
    }

}
