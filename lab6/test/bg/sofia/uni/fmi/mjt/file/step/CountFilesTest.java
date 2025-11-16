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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountFilesTest {
    @Mock
    private File file;

    private CountFiles countFiles;

    @BeforeEach
    void setUp() {
        countFiles = new CountFiles();
    }

    @Test
    void testProcessWithNull() {
        assertThrows(IllegalArgumentException.class, () -> countFiles.process(null),
                "IllegalArgumentException must be thrown when calling process with null");
    }

    @Test
    void testProcessWithZeroFiles() {
        Collection<File> files = new ArrayList<>();

        assertEquals(0, countFiles.process(files),
                "When calling process with empty collection it should return 0");
    }

    @Test
    void testProcessWithMoreThanOneFile() {
        Collection<File> files = new ArrayList<>();
        files.add(file);
        files.add(file);

        assertEquals(2, countFiles.process(files),
                "When calling process with collection with more then 1 file it should return the actual count of the files in the collection");
    }

}