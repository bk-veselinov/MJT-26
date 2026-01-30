package bg.sofia.uni.fmi.mjt.authserver.command.guest;

import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidCommandException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {

    @Test
    void testConstructorThrowsForInvalidSpecificCommand() {
        assertThrows(InvalidCommandException.class, () -> new HelpCommand("--command unknownCommand"),
                "constructor should throw InvalidCommandException for unknown specific command");
    }
    
}