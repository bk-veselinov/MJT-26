package bg.sofia.uni.fmi.mjt.space.loader;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LoaderTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testLoadObjFromCSVWithReaderNull() {
        assertThrows(IllegalArgumentException.class,
                () -> Loader.loadObjFromCSV(null, str -> str),
                "Should throw IllegalArgumentException when reader is null");
    }

    @Test
    void testLoadObjFromCSVWithProducerFunctionNull() {
        Reader reader = new StringReader("data");
        assertThrows(IllegalArgumentException.class,
                () -> Loader.loadObjFromCSV(reader, null),
                "Should throw IllegalArgumentException when producer is null");
    }

    @Test
    void testLoadObjFromCSVWithRocketData() {
        String csvData = """
                    "",Name,Wiki,Rocket Height
                    0,Tsyklon-3,wiki,39.0 m
                    1,Tsyklon-4M,wiki,38.7 m
                """;

        Reader reader = new StringReader(csvData);

        List<Rocket> results = Loader.loadObjFromCSV(reader, Rocket::of);

        assertEquals(2, results.size(), "Should have 2 items (header line skipped)");
        assertEquals(
                new Rocket("0", "Tsyklon-3", Optional.of("wiki"), Optional.of(39.0)),
                results.get(0));
        assertEquals(
                new Rocket("1", "Tsyklon-4M", Optional.of("wiki"), Optional.of(38.7)),
                results.get(1));
    }
}