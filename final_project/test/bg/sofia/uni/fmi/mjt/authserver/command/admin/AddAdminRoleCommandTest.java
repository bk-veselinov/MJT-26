package bg.sofia.uni.fmi.mjt.authserver.command.admin;

import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InactiveSessionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logevent.LogEvent;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddAdminRoleCommandTest {

    @Mock
    private AuthenticationManager manager;

    @Mock
    private SelectionKey key;

    @Mock
    private AuditLog logger;

    @Mock
    private SocketChannel socketChannel;

    private ConnectionData connectionData;

    private static final String SESSION_ID = "session-123";
    private static final String USER_ID = "user-456";

    @BeforeEach
    void setUp() {
        connectionData = new ConnectionData(null);
        connectionData.registerSuccessfulLogin(SESSION_ID, USER_ID);
    }

    @Test
    void testExecuteAddsAdminRoleSuccessfully() throws IOException {
        when(key.attachment()).thenReturn(connectionData);
        when(key.channel()).thenReturn(socketChannel);
        when(socketChannel.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 8080));

        AddAdminRoleCommand command = new AddAdminRoleCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        String result = command.execute(manager, key, logger);

        assertTrue(result.contains("targetUser"),
                "execute should return message containing the target username");
        assertTrue(result.contains("Admin"),
                "execute should return message indicating admin role was added");
    }

    @Test
    void testExecuteCallsManagerAddAdminRole() throws IOException {
        when(key.attachment()).thenReturn(connectionData);
        when(key.channel()).thenReturn(socketChannel);
        when(socketChannel.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 8080));

        AddAdminRoleCommand command = new AddAdminRoleCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        command.execute(manager, key, logger);

        verify(manager).addAdminRole(SESSION_ID, "targetUser");
    }

    @Test
    void testExecuteLogsEventBeforeAndAfterSuccess() throws IOException {
        when(key.attachment()).thenReturn(connectionData);
        when(key.channel()).thenReturn(socketChannel);
        when(socketChannel.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 8080));

        AddAdminRoleCommand command = new AddAdminRoleCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        command.execute(manager, key, logger);

        verify(logger, times(2)).logEvent(any(LogEvent.class));
    }

    @Test
    void testExecuteThrowsWhenNotLoggedIn() throws IOException {
        when(key.channel()).thenReturn(socketChannel);
        when(socketChannel.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 8080));

        ConnectionData notLoggedIn = new ConnectionData(null);
        when(key.attachment()).thenReturn(notLoggedIn);

        AddAdminRoleCommand command = new AddAdminRoleCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        assertThrows(InvalidActionException.class, () -> command.execute(manager, key, logger),
                "execute should throw when user is not logged in");
    }

    @Test
    void testExecuteClearsConnectionDataOnInactiveSession() throws IOException {
        when(key.attachment()).thenReturn(connectionData);
        when(key.channel()).thenReturn(socketChannel);
        when(socketChannel.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 8080));

        AddAdminRoleCommand command = new AddAdminRoleCommand(
                "--session-id " + SESSION_ID + " --username targetUser");

        doThrow(new InactiveSessionException("expired"))
                .when(manager).addAdminRole(SESSION_ID, "targetUser");

        assertThrows(InactiveSessionException.class, () -> command.execute(manager, key, logger),
                "execute should throw InactiveSessionException for inactive session");
        assertFalse(connectionData.isLogged(),
                "ConnectionData should be cleared after InactiveSessionException");
    }

    @Test
    void testConstructorThrowsForInvalidParameters() {
        assertThrows(InvalidCommandException.class,
                () -> new AddAdminRoleCommand("--session-id s"),
                "constructor should throw InvalidCommandException for incomplete parameters");
    }
}
