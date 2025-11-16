package bg.sofia.uni.fmi.mjt.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileTest {

    @Test
    void testFileConstructorWithNullData() {
        assertThrows(IllegalArgumentException.class, () -> new File(null),
                "Initializing File with null must throw IllegalArgumentException");
    }
}