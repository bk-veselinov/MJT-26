package bg.sofia.uni.fmi.mjt.authserver.command;

import bg.sofia.uni.fmi.mjt.authserver.command.admin.AddAdminRoleCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.admin.DeleteUserCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.admin.RemoveAdminRoleCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.guest.HelpCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.guest.LoginCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.guest.RegisterCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.user.ChangePasswordCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.user.LogoutCommand;
import bg.sofia.uni.fmi.mjt.authserver.command.user.UpdateUserDataCommand;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CommandFactoryTest {

    @Test
    void testNewCommandCreatesRegisterCommand() {
        Command command = CommandFactory.newCommand(
                "register --username u --password p --first-name f --last-name l --email e");
        assertInstanceOf(RegisterCommand.class, command,
                "newCommand should create RegisterCommand for 'register' input");
    }

    @Test
    void testNewCommandCreatesLoginCommandWithCredentials() {
        Command command = CommandFactory.newCommand("login --username u --password p");
        assertInstanceOf(LoginCommand.class, command,
                "newCommand should create LoginCommand for 'login' input with credentials");
    }

    @Test
    void testNewCommandCreatesLoginCommandWithSessionId() {
        Command command = CommandFactory.newCommand("login --session-id s");
        assertInstanceOf(LoginCommand.class, command,
                "newCommand should create LoginCommand for 'login' input with session-id");
    }

    @Test
    void testNewCommandCreatesUpdateUserDataCommand() {
        Command command = CommandFactory.newCommand("update-user --session-id s");
        assertInstanceOf(UpdateUserDataCommand.class, command,
                "newCommand should create UpdateUserDataCommand for 'update-user' input");
    }

    @Test
    void testNewCommandCreatesChangePasswordCommand() {
        Command command = CommandFactory.newCommand(
                "reset-password --session-id s --username u --old-password o --new-password n");
        assertInstanceOf(ChangePasswordCommand.class, command,
                "newCommand should create ChangePasswordCommand for 'reset-password' input");
    }

    @Test
    void testNewCommandCreatesLogoutCommand() {
        Command command = CommandFactory.newCommand("logout --session-id s");
        assertInstanceOf(LogoutCommand.class, command,
                "newCommand should create LogoutCommand for 'logout' input");
    }

    @Test
    void testNewCommandCreatesAddAdminRoleCommand() {
        Command command = CommandFactory.newCommand("add-admin-user --session-id s --username u");
        assertInstanceOf(AddAdminRoleCommand.class, command,
                "newCommand should create AddAdminRoleCommand for 'add-admin-user' input");
    }

    @Test
    void testNewCommandCreatesRemoveAdminRoleCommand() {
        Command command = CommandFactory.newCommand("remove-admin-user --session-id s --username u");
        assertInstanceOf(RemoveAdminRoleCommand.class, command,
                "newCommand should create RemoveAdminRoleCommand for 'remove-admin-user' input");
    }

    @Test
    void testNewCommandCreatesDeleteUserCommand() {
        Command command = CommandFactory.newCommand("delete-user --session-id s --username u");
        assertInstanceOf(DeleteUserCommand.class, command,
                "newCommand should create DeleteUserCommand for 'delete-user' input");
    }

    @Test
    void testNewCommandCreatesHelpCommand() {
        Command command = CommandFactory.newCommand("help");
        assertInstanceOf(HelpCommand.class, command,
                "newCommand should create HelpCommand for 'help' input");
    }

    @Test
    void testNewCommandThrowsForInvalidCommand() {
        assertThrows(InvalidCommandException.class, () -> CommandFactory.newCommand("invalidCommand"),
                "newCommand should throw InvalidCommandException for invalid command string");
    }
}