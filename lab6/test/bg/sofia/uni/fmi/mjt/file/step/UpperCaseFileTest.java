package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpperCaseFileTest {
    @Mock
    private File file;

    private UpperCaseFile upperCaseFile;

    @BeforeEach
    void setUp() {
        upperCaseFile = new UpperCaseFile();
    }

    @Test
    void testProcessWithNull() {
        assertThrows(IllegalArgumentException.class, () -> upperCaseFile.process(null),
                "When process is called with null it should throw IllegalArgumentException");
    }

    @Test
    void testProcessWithValidFile() {
        when(file.getContent()).thenReturn("data");

        File newFile = upperCaseFile.process(file);
        assertAll(
                () -> assertNotSame(file, upperCaseFile.process(file),
                        "when calling process a new file obj must be returned"),
                () -> assertEquals("DATA", newFile.getContent(),
                        "when calling process the new file object must have the content of the original file obj but uppercase")
        );

    }

}