package bg.sofia.uni.fmi.mjt.file.step;

import bg.sofia.uni.fmi.mjt.file.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SplitFileTest {

    @Mock
    private File file;


    private SplitFile splitFile;

    @BeforeEach
    void setUp() {
        splitFile = new SplitFile();
    }

    @Test
    void testProcessWithNullInput() {
        assertThrows(IllegalArgumentException.class, () -> splitFile.process(null),
                "when process is called with null IllegalArgumentException must be thrown");
    }

    @Test
    void testProcessWithFileWithWhiteSpace() {
        when(file.getContent()).thenReturn("   foo bar   ");

        File file1 = new File("foo");

        File file2 = new File("bar");


        Set<File> result = splitFile.process(file);

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(file1)),
                () -> assertTrue(result.contains(file2))
        );
    }
}