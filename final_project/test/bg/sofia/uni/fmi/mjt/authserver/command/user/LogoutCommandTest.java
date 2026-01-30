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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LogoutCommandTest {

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
    void testExecuteLogsOutSuccessfully() {
        when(key.attachment()).thenReturn(connectionData);

        LogoutCommand command = new LogoutCommand("--session-id " + SESSION_ID);

        String result = command.execute(manager, key, logger);

        assertTrue(result.contains("no longer logged in"),
                "execute should return success message on logout");
    }

    @Test
    void testExecuteCallsManagerLogoutUser() {
        when(key.attachment()).thenReturn(connectionData);

        LogoutCommand command = new LogoutCommand("--session-id " + SESSION_ID);

        command.execute(manager, key, logger);

        verify(manager).logoutUser(SESSION_ID);
    }

    @Test
    void testExecuteClearsConnectionData() {
        when(key.attachment()).thenReturn(connectionData);

        LogoutCommand command = new LogoutCommand("--session-id " + SESSION_ID);

        command.execute(manager, key, logger);

        assertFalse(connectionData.isLogged(),
                "ConnectionData should be cleared after logout");
    }

    @Test
    void testExecuteThrowsWhenNotLoggedIn() {
        ConnectionData notLoggedIn = new ConnectionData(null);
        when(key.attachment()).thenReturn(notLoggedIn);

        LogoutCommand command = new LogoutCommand("--session-id " + SESSION_ID);

        assertThrows(UnauthenticatedUserException.class, () -> command.execute(manager, key, logger),
                "execute should throw UnauthenticatedUserException when not logged in");
    }

    @Test
    void testExecuteThrowsForMismatchedSessionId() {
        when(key.attachment()).thenReturn(connectionData);
        
        LogoutCommand command = new LogoutCommand("--session-id wrongSessionId");

        assertThrows(InvalidSessionIdException.class, () -> command.execute(manager, key, logger),
                "execute should throw InvalidSessionIdException for mismatched session id");
    }

    @Test
    void testConstructorThrowsForInvalidParameters() {
        assertThrows(InvalidCommandException.class,
                () -> new LogoutCommand("--invalid params"),
                "constructor should throw InvalidCommandException for invalid parameters");
    }
}
