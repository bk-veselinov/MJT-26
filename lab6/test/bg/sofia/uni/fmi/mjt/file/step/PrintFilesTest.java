package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PrintFilesTest {

    @Mock
    private File file;


    private PrintFiles printFiles;

    @BeforeEach
    void setUp() {
        printFiles = new PrintFiles();
    }

    @Test
    void testProcessWithNull() {
        assertThrows(IllegalArgumentException.class, () -> printFiles.process(null),
                "calling process with null must throw IllegalArgumentException");
    }

    @Test
    void testProcessWithValidInput() {
        Collection<File> files = new ArrayList<>();
        files.add(file);

        assertIterableEquals(files, printFiles.process(files),
                "process must return the same collection");
    }
}