package bg.sofia.uni.fmi.mjt.authserver.command.admin;

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
class DeleteUserCommandTest {

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
    void testExecuteDeletesUserSuccessfully() {
        when(key.attachment()).thenReturn(connectionData);

        DeleteUserCommand command = new DeleteUserCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        String result = command.execute(manager, key, logger);

        assertTrue(result.contains("targetUser"),
                "execute should return message containing the target username");
        assertTrue(result.contains("was deleted"),
                "execute should return message indicating user was deleted");
    }

    @Test
    void testExecuteCallsManagerRemoveUser() {
        when(key.attachment()).thenReturn(connectionData);

        DeleteUserCommand command = new DeleteUserCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        command.execute(manager, key, logger);

        verify(manager).removeUser(SESSION_ID, "targetUser");
    }

    @Test
    void testExecuteThrowsWhenNotLoggedIn() {
        ConnectionData notLoggedIn = new ConnectionData(null);
        when(key.attachment()).thenReturn(notLoggedIn);

        DeleteUserCommand command = new DeleteUserCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        assertThrows(UnauthenticatedUserException.class, () -> command.execute(manager, key, logger),
                "execute should throw UnauthenticatedUserException when not logged in");
    }

    @Test
    void testExecuteThrowsForMismatchedSessionId() {
        when(key.attachment()).thenReturn(connectionData);

        DeleteUserCommand command = new DeleteUserCommand(
                "--session-id wrongSessionId --username targetUser");

        assertThrows(InvalidSessionIdException.class, () -> command.execute(manager, key, logger),
                "execute should throw InvalidSessionIdException for mismatched session id");
    }

    @Test
    void testExecuteClearsConnectionDataOnInactiveSession() {
        when(key.attachment()).thenReturn(connectionData);
        
        DeleteUserCommand command = new DeleteUserCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        doThrow(new InactiveSessionException("expired"))
                .when(manager).removeUser(SESSION_ID, "targetUser");

        assertThrows(InactiveSessionException.class, () -> command.execute(manager, key, logger),
                "execute should throw InactiveSessionException for inactive session");
        assertFalse(connectionData.isLogged(),
                "ConnectionData should be cleared after InactiveSessionException");
    }

    @Test
    void testConstructorThrowsForInvalidParameters() {
        assertThrows(InvalidCommandException.class,
                () -> new DeleteUserCommand("--session-id s"),
                "constructor should throw InvalidCommandException for incomplete parameters");
    }
}