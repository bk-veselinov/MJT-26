package bg.sofia.uni.fmi.mjt.authserver.command;

import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SelectionKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuditLog logger;

    @Mock
    private Command command;

    @Mock
    private SelectionKey key;

    private CommandExecutor commandExecutor;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        commandExecutor = new CommandExecutor(authenticationManager, executorService, logger);
    }

    @Test
    void testExecuteCommandReturnsSuccessMessage() throws ExecutionException, InterruptedException {
        when(command.execute(authenticationManager, key, logger)).thenReturn("Success");

        CompletableFuture<String> future = commandExecutor.executeCommand(command, key);
        String result = future.get();

        assertEquals("Success", result, "executeCommand should return the command's result");
    }

    @Test
    void testExecuteCommandReturnsErrorMessageOnRuntimeException()
            throws ExecutionException, InterruptedException {
        when(command.execute(authenticationManager, key, logger))
                .thenThrow(new RuntimeException("test error"));

        CompletableFuture<String> future = commandExecutor.executeCommand(command, key);
        String result = future.get();

        assertTrue(result.contains("test error"),
                "executeCommand should return error message containing the exception message");
    }

}
