package bg.sofia.uni.fmi.mjt.authserver.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    private static final String COMMAND_REGEX = "(?<command>^[A-Za-z\\-]+)";
    private static final String ALL_PARAMETERS_REGEX = "(?<command>^[A-Za-z\\-]+)\\s+(?<parameters>.+)";

    public static String getCommand(String commandData) {
        Matcher matcher = getMatcher(COMMAND_REGEX, commandData);

        return getGroup(matcher, "command");
    }

    public static String getParameters(String commandData) {
        Matcher matcher = getMatcher(ALL_PARAMETERS_REGEX, commandData);

        return getGroup(matcher, "parameters");
    }

    public static String getParameter(String commandData, String parameterName) {
        String regex = "(?<parameterName>--" + parameterName + ")\\s+(?<data>\\S+)";
        Matcher matcher = getMatcher(regex, commandData);

        return getGroup(matcher, "data");

    }

    public static boolean isCommandValid(String regex, String commandData) {
        Matcher matcher = getMatcher(regex, commandData);
        return matcher.find();
    }

    private static Matcher getMatcher(String regex, String commandData) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(commandData);
    }

    private static String getGroup(Matcher matcher, String groupName) {
        if (matcher.find()) {
            return matcher.group(groupName);
        }
        return null;
    }

}
