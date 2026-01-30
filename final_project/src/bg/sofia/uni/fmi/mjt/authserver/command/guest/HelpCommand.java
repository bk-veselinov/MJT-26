package bg.sofia.uni.fmi.mjt.authserver.command.guest;

import bg.sofia.uni.fmi.mjt.authserver.util.CommandParser;
import bg.sofia.uni.fmi.mjt.authserver.command.Command;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class HelpCommand extends Command {

    private String specificCommand;

    private static final String COMMAND_REGEX = "^(\\s*--command\\s+\\S+)?$";
    private static final String MESSAGE_HEADER = "Command information:\n";
    private static final String SYNTAX_HEADER = "Syntax:";
    private static final String DESCRIPTION_HEADER = "Description:";
    private static final String IMPORTANT_NOTE = "\n!Note: " +
            "The use of space(\" \") is not allowed in any of the fields.";

    private static final Map<String, String> FUNCTION_SYNTAX = Map.of(
            "register", "register --username <username> --password <password>" +
                    " --first-name <firstName> --last-name <lastName> --email <email>",
            "login", "login --username <username> --password <password>\n" +
                    "login --session-id <sessionId>",
            "update-user", "update-user --session-id <session-id>" +
                    " --new-username <newUsername> --new-first-name <newFirstName>" +
                    " --new-last-name <newLastName> --new-email <email>.",
            "reset-password", "reset-password --session-id <session-id>" +
                    "--username <username> --old-password <oldPassword> --new-password <newPassword>",
            "logout", "logout --session-id <sessionId>",
            "add-admin-user", "add-admin-user --session-id <sessionId> --username <username>",
            "remove-admin-user", "remove-admin-user --session-id <sessionId> --username <username>",
            "delete-user", "delete-user --session-id <sessionId> --username <username>"
    );
    private static final Map<String, String> FUNCTION_DESCRIPTION = Map.of(
            "register", "Регистрация в системата. Всички полета са задължителни.",
            "login", "Вход в системата чрез име и парола или чрез id на активна сесия. Всички полета са задължителни.",
            "update-user", "Промяна на потребителските данни. Само session-id е задължително поле.",
            "reset-password", "Промяна на парола. Всички полета са задължителни.",
            "logout", "Изход от системата.",
            "add-admin-user", "Добавяне на администраторски права на потребител. " +
                    "Нужни са администраторски права за изпълняване на тази команда.",
            "remove-admin-user", "Премахване на администраторски права на потребител. " +
                    "Нужни са администраторски права за изпълняване на тази команда.",
            "delete-user", "Премахване на потребител. " +
                    "Нужни са администраторски права за изпълняване на тази команда."
    );

    public HelpCommand(String inputParameters) {
        super(inputParameters);
        if (inputParameters != null) {
            validateCommand(COMMAND_REGEX);
            parseInputParameters();
        }
    }

    @Override
    public String execute(AuthenticationManager authenticationManager, SelectionKey key, AuditLog logger) {
        if (specificCommand != null) {
            return MESSAGE_HEADER + getFunctionInformation(specificCommand) + IMPORTANT_NOTE;
        }

        StringBuilder output = new StringBuilder(MESSAGE_HEADER);
        for (String functionName : FUNCTION_SYNTAX.keySet()) {
            output.append(getFunctionInformation(functionName));
        }

        return output + IMPORTANT_NOTE;
    }

    @Override
    protected void parseInputParameters() {
        specificCommand = CommandParser.getParameter(inputParameters, "command");
        if (!FUNCTION_DESCRIPTION.containsKey(specificCommand)) {
            throw new InvalidCommandException("The command is not valid. Type \"help\" to see all commands " +
                    "or \"help --command <command name>\" to get information for a specific command.");
        }
    }

    private String getFunctionInformation(String functionName) {
        return String.join("\n",
                SYNTAX_HEADER, "\t" + FUNCTION_SYNTAX.get(functionName),
                DESCRIPTION_HEADER, "\t" + FUNCTION_DESCRIPTION.get(functionName)) + "\n";
    }
}
