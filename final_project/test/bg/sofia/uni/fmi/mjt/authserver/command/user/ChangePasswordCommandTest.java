package bg.sofia.uni.fmi.mjt.authserver.command.user;

import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InactiveSessionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidSessionIdException;
import bg.sofia.uni.fmi.mjt.authserver.exception.UnauthenticatedUserException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangePasswordCommandTest {

    @Mock
    private AuthenticationManager manager;

    @Mock
    private SelectionKey key;

    @Mock
    private AuditLog logger;

    private ConnectionData connectionData;

    private static final String SESSION_ID = "session-123";
    private static final String USER_ID = "user-456";

    @BeforeEach
    void setUp() {
        connectionData = new ConnectionData(null);
        connectionData.registerSuccessfulLogin(SESSION_ID, USER_ID);
    }

    @Test
    void testExecuteChangesPasswordSuccessfully() {
        when(key.attachment()).thenReturn(connectionData);

        ChangePasswordCommand command = new ChangePasswordCommand(
                "--session-id " + SESSION_ID + " --username testUser --old-password old --new-password new");

        String result = command.execute(manager, key, logger);

        assertTrue(result.contains("Password was changed"),
                "execute should return success message on password change");
    }

    @Test
    void testExecuteCallsManagerChangePassword() {
        when(key.attachment()).thenReturn(connectionData);

        ChangePasswordCommand command = new ChangePasswordCommand(
                "--session-id " + SESSION_ID + " --username testUser --old-password old --new-password new");

        command.execute(manager, key, logger);

        verify(manager).changePassword(SESSION_ID, "testUser", "old", "new");
    }

    @Test
    void testExecuteThrowsWhenNotLoggedIn() {
        ConnectionData notLoggedIn = new ConnectionData(null);
        when(key.attachment()).thenReturn(notLoggedIn);

        ChangePasswordCommand command = new ChangePasswordCommand(
                "--session-id " + SESSION_ID + " --username testUser --old-password old --new-password new");

        assertThrows(UnauthenticatedUserException.class, () -> command.execute(manager, key, logger),
                "execute should throw UnauthenticatedUserException when not logged in");
    }

    @Test
    void testExecuteThrowsForMismatchedSessionId() {
        when(key.attachment()).thenReturn(connectionData);

        ChangePasswordCommand command = new ChangePasswordCommand(
                "--session-id wrongSessionId --username testUser --old-password old --new-password new");

        assertThrows(InvalidSessionIdException.class, () -> command.execute(manager, key, logger),
                "execute should throw InvalidSessionIdException for mismatched session id");
    }

    @Test
    void testExecuteClearsConnectionDataOnInactiveSession() {
        when(key.attachment()).thenReturn(connectionData);

        ChangePasswordCommand command = new ChangePasswordCommand(
                "--session-id " + SESSION_ID + " --username testUser --old-password old --new-password new");

        doThrow(new InactiveSessionException("expired"))
                .when(manager).changePassword(SESSION_ID, "testUser", "old", "new");

        assertThrows(InactiveSessionException.class, () -> command.execute(manager, key, logger),
                "execute should throw InactiveSessionException for inactive session");
        assertFalse(connectionData.isLogged(),
                "ConnectionData should be cleared after InactiveSessionException");
    }

    @Test
    void testConstructorThrowsForInvalidParameters() {
        assertThrows(InvalidCommandException.class,
                () -> new ChangePasswordCommand("--session-id s"),
                "constructor should throw InvalidCommandException for invalid parameters");
    }
}