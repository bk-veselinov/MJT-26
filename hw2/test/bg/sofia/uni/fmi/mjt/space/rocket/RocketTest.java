package bg.sofia.uni.fmi.mjt.space.rocket;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RocketTest {

    @Test
    void testRocketOfWithProperData() {
        String line = "1,r1,wiki,70.0 m";

        Rocket rocket = Rocket.of(line);

        assertAll(
                () -> assertEquals("1", rocket.id(), "ID should match"),
                () -> assertEquals("r1", rocket.name(), "Name should match"),
                () -> assertEquals(Optional.of("wiki"), rocket.wiki(), "Wiki should be present"),
                () -> assertEquals(Optional.of(70.0), rocket.height(), "Height should be parsed correctly ignoring the unit")
        );
    }

    @Test
    void testRocketOfWithNoHeight() {
        String line = "1,r1,wiki,";

        Rocket rocket = Rocket.of(line);

        assertAll(
                () -> assertEquals("1", rocket.id()),
                () -> assertEquals("r1", rocket.name()),
                () -> assertTrue(rocket.wiki().isPresent()),
                () -> assertTrue(rocket.height().isEmpty(), "Height should be empty when it is not present in the string data")
        );
    }

    @Test
    void testRocketOfWithNoHeightNoWiki() {
        String line = "1,r1,,";

        Rocket rocket = Rocket.of(line);

        assertAll(
                () -> assertEquals("1", rocket.id()),
                () -> assertEquals("r1", rocket.name()),
                () -> assertTrue(rocket.wiki().isEmpty()),
                () -> assertTrue(rocket.height().isEmpty(), "Height should be empty when it is not present in the string data")
        );
    }

    @Test
    void testRocketOfWithNoWiki() {
        String line = "1,r1,,70.0 m";

        Rocket rocket = Rocket.of(line);

        assertAll(
                () -> assertEquals("1", rocket.id()),
                () -> assertEquals("r1", rocket.name()),
                () -> assertTrue(rocket.wiki().isEmpty(), "Wiki should be empty when it is not present in the string data"),
                () -> assertEquals(Optional.of(70.0), rocket.height())
        );
    }
}