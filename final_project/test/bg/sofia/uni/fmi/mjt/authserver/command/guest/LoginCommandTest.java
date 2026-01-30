package bg.sofia.uni.fmi.mjt.authserver.command.guest;

import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.entity.Session;
import bg.sofia.uni.fmi.mjt.authserver.exception.BannedUserException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.authserver.exception.LoginException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginCommandTest {

    @Mock
    private AuthenticationManager manager;

    @Mock
    private SelectionKey key;

    @Mock
    private AuditLog logger;

    @Mock
    private SocketChannel socketChannel;

    private ConnectionData connectionData;

    @BeforeEach
    void setUp() {
        connectionData = new ConnectionData(null);
    }

    void setStubbing() throws IOException {
        when(key.attachment()).thenReturn(connectionData);
        when(key.channel()).thenReturn(socketChannel);
        when(socketChannel.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 8080));
    }

    @Test
    void testExecuteLoginWithCredentialsSuccessfully() throws IOException {
        setStubbing();
        LoginCommand command = new LoginCommand("--username testUser --password testPass");
        Session session = new Session("userId");
        when(manager.loginUser("testUser", "testPass")).thenReturn(session);

        String result = command.execute(manager, key, logger);

        assertTrue(result.contains("Welcome back!"),
                "execute should return a message containing the username");
        assertTrue(result.contains(session.getId()),
                "execute should return a message containing the session ID");
    }

    @Test
    void testExecuteThrowsWhenUserIsBanned() {
        when(key.attachment()).thenReturn(connectionData);

        connectionData.registerUnsuccessfulLogin();
        for (int i = 0; i < 10; i++) {
            connectionData.registerUnsuccessfulLogin();
        }

        LoginCommand command = new LoginCommand("--username testUser --password testPass");

        assertThrows(BannedUserException.class, () -> command.execute(manager, key, logger),
                "execute should throw BannedUserException when user is banned");
    }

    @Test
    void testExecuteRegistersUnsuccessfulLoginOnFailure() throws IOException {
        setStubbing();
        LoginCommand command = new LoginCommand("--username testUser --password testPass");
        when(manager.loginUser("testUser", "testPass"))
                .thenThrow(new RuntimeException("invalid credentials"));

        assertThrows(LoginException.class, () -> command.execute(manager, key, logger),
                "execute should throw LoginException on login failure");
    }

    @Test
    void testConstructorThrowsForInvalidParameters() {
        assertThrows(InvalidCommandException.class, () -> new LoginCommand("--invalid params"),
                "constructor should throw InvalidCommandException for invalid parameters");
    }
}
