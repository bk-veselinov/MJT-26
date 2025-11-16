package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import bg.sofia.uni.fmi.mjt.file.exception.EmptyFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckEmptyFileTest {
    @Mock
    private File file;

    private CheckEmptyFile checkEmptyFile;

    @BeforeEach
    void setUp() {
        checkEmptyFile = new CheckEmptyFile();
    }

    @Test
    void testProcessWithEmptyFile() {
        when(file.getContent()).thenReturn("");

        assertThrows(EmptyFileException.class, () -> checkEmptyFile.process(file),
                "process must throw EmptyFileException when an empty file is passed as an argument");
    }

    @Test
    void testProcessWithFileWithNullContent() {
        when(file.getContent()).thenReturn(null);

        assertThrows(EmptyFileException.class, () -> checkEmptyFile.process(file),
                "process must throw EmptyFileException when a file with content=null  is passed as an argument");
    }

    @Test
    void testProcessWithNull() {
        assertThrows(EmptyFileException.class, () -> checkEmptyFile.process(null),
                "process must throw EmptyFileException when null is passed as an argument");
    }

    @Test
    void testProcessWithValidFile() {
        when(file.getContent()).thenReturn("data");

        assertSame(file, checkEmptyFile.process(file),
                "Passing a valid(non-empty) file as a parameter must return the same file obj");
    }
}