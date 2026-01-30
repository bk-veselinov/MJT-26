package bg.sofia.uni.fmi.mjt.authserver.command;

import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;

public enum CommandType {

    REGISTER("register"),
    LOGIN("login"),
    UPDATE_USER("update-user"),
    RESET_PASSWORD("reset-password"),
    LOGOUT("logout"),
    ADD_ADMIN_USER("add-admin-user"),
    REMOVE_ADMIN_USER("remove-admin-user"),
    DELETE_USER("delete-user"),
    HELP("help");

    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static CommandType fromString(String text) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.command.equalsIgnoreCase(text)) {
                return commandType;
            }
        }

        throw new InvalidCommandException("The command is not valid. Type \"help\" to see all commands " +
                "or \"help --command <command name>\" to get information for a specific command.");
    }

}
