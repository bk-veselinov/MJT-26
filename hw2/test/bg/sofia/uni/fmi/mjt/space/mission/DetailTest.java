package bg.sofia.uni.fmi.mjt.space.mission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DetailTest {

    @Test
    void testDetailOfWithProperData() {
        String input = "Falcon 9 Block 5 | Starlink V1 L9 & BlackSky";

        Detail detail = Detail.of(input);

        assertAll(
                () -> assertEquals("Falcon 9 Block 5", detail.rocketName()),
                () -> assertEquals("Starlink V1 L9 & BlackSky", detail.payload())
        );
    }
}