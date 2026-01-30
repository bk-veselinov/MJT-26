package bg.sofia.uni.fmi.mjt.authserver.command;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class Command {

    private static final String INVALID_COMMAND_ERROR_MESSAGE =
            "The command is not valid. Type \"help\" to see all commands " +
                    "or \"help --command <command name>\" to get information for a specific command.";

    protected final String inputParameters;

    public Command(String inputParameters) {
        this.inputParameters = inputParameters;
    }

    public abstract String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger);

    protected abstract void parseInputParameters();

    protected void validateCommand(String commandRegex) {
        if (!CommandParser.isCommandValid(commandRegex, inputParameters)) {
            throw new InvalidCommandException(INVALID_COMMAND_ERROR_MESSAGE);
        }
    }

    protected ConnectionData getConnectionData(SelectionKey key) {
        return (ConnectionData) key.attachment();
    }

    protected String getUserIP(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            return ((InetSocketAddress) client.getRemoteAddress()).getAddress().getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException("Problem while extracting user IP. Please contact support.", e);
        }
    }

}
