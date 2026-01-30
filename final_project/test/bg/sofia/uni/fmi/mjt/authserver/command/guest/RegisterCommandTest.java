package bg.sofia.uni.fmi.mjt.authserver.command.guest;

import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.entity.Session;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RegisterCommandTest {

    @Mock
    private AuthenticationManager manager;

    @Mock
    private SelectionKey key;

    @Mock
    private AuditLog logger;

    private ConnectionData connectionData;

    @BeforeEach
    void setUp() {
        connectionData = new ConnectionData(null);
    }

    @Test
    void testExecuteRegistersUserSuccessfully() {
        when(key.attachment()).thenReturn(connectionData);

        RegisterCommand command = new RegisterCommand(
                "--username newUser --password pass --first-name fName --last-name lName --email test@test.com");
        Session session = new Session("userId");
        when(manager.registerUser("newUser", "pass", "fName", "lName", "test@test.com")).thenReturn(session);

        String result = command.execute(manager, key, logger);

        assertTrue(result.contains("newUser"),
                "execute should return a message containing the username");
        assertTrue(result.contains(session.getId()),
                "execute should return a message containing the session ID");
    }

    @Test
    void testExecuteThrowsWhenUserAlreadyLoggedIn() {
        when(key.attachment()).thenReturn(connectionData);
        connectionData.registerSuccessfulLogin("sessionId", "userId");

        RegisterCommand command = new RegisterCommand(
                "--username newUser --password pass --first-name fName --last-name lName --email test@test.com");

        assertThrows(InvalidActionException.class, () -> command.execute(manager, key, logger),
                "execute should throw InvalidActionException when user is already logged in");
    }

    @Test
    void testExecuteRegistersSuccessfulLoginInConnectionData() {
        when(key.attachment()).thenReturn(connectionData);

        RegisterCommand command = new RegisterCommand(
                "--username newUser --password pass --first-name fName --last-name lName --email test@test.com");
        Session session = new Session("userId");
        when(manager.registerUser("newUser", "pass", "fName", "lName", "test@test.com")).thenReturn(session);

        command.execute(manager, key, logger);

        assertTrue(connectionData.isLogged(),
                "ConnectionData should be logged after successful registration");
    }

    @Test
    void testConstructorThrowsForInvalidParameters() {
        assertThrows(InvalidCommandException.class,
                () -> new RegisterCommand("--username user"),
                "constructor should throw InvalidCommandException for invalid command");
    }
}