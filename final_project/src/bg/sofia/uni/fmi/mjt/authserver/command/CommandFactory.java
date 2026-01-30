package bg.sofia.uni.fmi.mjt.authserver.command;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.command.admin.AddAdminRoleCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.admin.DeleteUserCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.admin.RemoveAdminRoleCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.user.ChangePasswordCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.user.LogoutCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.user.UpdateUserDataCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.guest.HelpCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.guest.LoginCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.guest.RegisterCommand;

public class CommandFactory {

    public static Command newCommand(String commandData) {
        CommandType commandType = CommandType.fromString(CommandParser.getCommand(commandData));
        String parameters = CommandParser.getParameters(commandData);

        return switch (commandType) {
            case REGISTER -> new RegisterCommand(parameters);
            case LOGIN -> new LoginCommand(parameters);
            case UPDATE_USER -> new UpdateUserDataCommand(parameters);
            case RESET_PASSWORD -> new ChangePasswordCommand(parameters);
            case LOGOUT -> new LogoutCommand(parameters);
            case ADD_ADMIN_USER -> new AddAdminRoleCommand(parameters);
            case REMOVE_ADMIN_USER -> new RemoveAdminRoleCommand(parameters);
            case DELETE_USER -> new DeleteUserCommand(parameters);
            case HELP -> new HelpCommand(parameters);
        };
    }

}
