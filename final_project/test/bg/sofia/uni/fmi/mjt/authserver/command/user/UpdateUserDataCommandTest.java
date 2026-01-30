package bg.sofia.uni.fmi.mjt.authserver.command.user;

import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
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
class UpdateUserDataCommandTest {

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
    void testExecuteUpdatesUserDataSuccessfully() {
        when(key.attachment()).thenReturn(connectionData);

        UpdateUserDataCommand command = new UpdateUserDataCommand(
                "--session-id " + SESSION_ID + " --new-username newName");

        String result = command.execute(manager, key, logger);

        assertTrue(result.contains("user data was changed"),
                "execute should return success message on update");
    }

    @Test
    void testExecuteCallsManagerUpdateUserData() {
        when(key.attachment()).thenReturn(connectionData);

        UpdateUserDataCommand command = new UpdateUserDataCommand(
                "--session-id " + SESSION_ID + " --new-username newName --new-email new@test.com");

        command.execute(manager, key, logger);

        verify(manager).updateUserData(SESSION_ID, "newName", null, null, "new@test.com");
    }

    @Test
    void testExecuteThrowsWhenNotLoggedIn() {
        ConnectionData notLoggedIn = new ConnectionData(null);
        when(key.attachment()).thenReturn(notLoggedIn);

        UpdateUserDataCommand command = new UpdateUserDataCommand(
                "--session-id " + SESSION_ID);

        assertThrows(UnauthenticatedUserException.class, () -> command.execute(manager, key, logger),
                "execute should throw UnauthenticatedUserException when not logged in");
    }

    @Test
    void testExecuteThrowsForMismatchedSessionId() {
        when(key.attachment()).thenReturn(connectionData);

        UpdateUserDataCommand command = new UpdateUserDataCommand(
                "--session-id wrongSessionId");

        assertThrows(InvalidSessionIdException.class, () -> command.execute(manager, key, logger),
                "execute should throw InvalidSessionIdException for mismatched session id");
    }

    @Test
    void testExecuteClearsConnectionDataOnInvalidSessionIdFromManager() {
        when(key.attachment()).thenReturn(connectionData);

        UpdateUserDataCommand command = new UpdateUserDataCommand(
                "--session-id " + SESSION_ID);

        doThrow(new InvalidSessionIdException("expired"))
                .when(manager).updateUserData(SESSION_ID, null, null, null, null);

        assertThrows(InvalidSessionIdException.class, () -> command.execute(manager, key, logger),
                "execute should throw InvalidSessionIdException from manager");
        assertFalse(connectionData.isLogged(),
                "ConnectionData should be cleared after InvalidSessionIdException");
    }

    @Test
    void testConstructorThrowsForInvalidParameters() {
        assertThrows(InvalidCommandException.class,
                () -> new UpdateUserDataCommand("--invalid params"),
                "constructor should throw InvalidCommandException for invalid parameters");
    }
}
