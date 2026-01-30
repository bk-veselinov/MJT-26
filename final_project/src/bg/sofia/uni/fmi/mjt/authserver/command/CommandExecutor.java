package bg.sofia.uni.fmi.mjt.authserver.command;

import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class CommandExecutor {

    private static final String RUNTIME_ERROR_MESSAGE = "Error while processing the command. ";

    private final AuthenticationManager authenticationManager;
    private final ExecutorService executorService;
    private final AuditLog logger;

    public CommandExecutor(AuthenticationManager authenticationManager,
                           ExecutorService executorService,
                           AuditLog logger) {
        this.authenticationManager = authenticationManager;
        this.executorService = executorService;
        this.logger = logger;
    }

    public CompletableFuture<String> executeCommand(Command command, SelectionKey key) {
        return CompletableFuture.supplyAsync(() -> processCommand(command, key), executorService);
    }

    private String processCommand(Command command, SelectionKey key) {
        try {
            if (key == null) {
                throw new IllegalArgumentException("sth wronng asd");
            }
            return command.execute(authenticationManager, key, logger);

        } catch (RuntimeException e) {
            return "Error while processing the command. " + e.getMessage();

        } catch (Exception e) {
            return "Server was unbale to process this command. Please contact support.";

        }
    }

}
