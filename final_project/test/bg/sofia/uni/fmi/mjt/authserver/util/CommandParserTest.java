package bg.sofia.uni.fmi.mjt.authserver.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    @Test
    void testGetCommandReturnsCommandName() {
        String result = CommandParser.getCommand("register --username test");
        assertEquals("register", result, "getCommand should return the command name");
    }

    @Test
    void testGetCommandReturnsHyphenatedCommand() {
        String result = CommandParser.getCommand("add-admin-user --session-id abc");
        assertEquals("add-admin-user", result, "getCommand should return hyphenated command names");
    }

    @Test
    void testGetCommandReturnsNullForEmptyInput() {
        String result = CommandParser.getCommand("");
        assertNull(result, "getCommand should return null for empty input");
    }

    @Test
    void testGetParametersReturnsAllParameters() {
        String result = CommandParser.getParameters("register --username test --password pass");
        assertEquals("--username test --password pass", result, "getParameters should return all parameters after command");
    }

    @Test
    void testGetParametersReturnsNullWhenNoParameters() {
        String result = CommandParser.getParameters("help");
        assertNull(result, "getParameters should return null when no parameters exist");
    }

    @Test
    void testGetParameterReturnsSpecificParameterValue() {
        String input = "--username testUser --password testPass";
        String result = CommandParser.getParameter(input, "username");
        assertEquals("testUser", result, "getParameter should return the value of the specified parameter");
    }

    @Test
    void testGetParameterReturnsNullForMissingParameter() {
        String input = "--username testUser";
        String result = CommandParser.getParameter(input, "password");
        assertNull(result, "getParameter should return null when parameter is not found");
    }

    @Test
    void testIsCommandValidReturnsTrueForValidCommand() {
        String regex = "^\\s*--username\\s+\\S+\\s+--password\\s+\\S+$";
        boolean result = CommandParser.isCommandValid(regex, "--username test --password pass");
        assertTrue(result, "isCommandValid should return true for valid input matching regex");
    }

    @Test
    void testIsCommandValidReturnsFalseForInvalidCommand() {
        String regex = "^\\s*--username\\s+\\S+\\s+--password\\s+\\S+$";
        boolean result = CommandParser.isCommandValid(regex, "--username test");
        assertFalse(result, "isCommandValid should return false when input does not match regex");
    }
}
