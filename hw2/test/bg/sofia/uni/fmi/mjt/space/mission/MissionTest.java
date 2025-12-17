package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {

    @Test
    void testMissionOfWithProperData() {
        String input = "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\"," +
                "\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive," +
                "\"50.0 \",Success";

        Mission mission = Mission.of(input);

        assertAll(
                () -> assertEquals("0", mission.id()),
                () -> assertEquals("SpaceX", mission.company()),
                () -> assertEquals("LC-39A, Kennedy Space Center, Florida, USA", mission.location()),
                () -> assertEquals(LocalDate.of(2020, 8, 7), mission.date()),
                () -> assertEquals(new Detail("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky"), mission.detail()),
                () -> assertEquals(RocketStatus.STATUS_ACTIVE, mission.rocketStatus()),
                () -> assertEquals(Optional.of(50.0), mission.cost()),
                () -> assertEquals(MissionStatus.SUCCESS, mission.missionStatus())
        );
    }

    @Test
    void testMissionOfWithNoCost() {
        String input = "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07," +
                " 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,,Success";

        Mission mission = Mission.of(input);

        assertTrue(mission.cost().isEmpty(),
                "Cost should be empty when it is not in the data string");

    }
}