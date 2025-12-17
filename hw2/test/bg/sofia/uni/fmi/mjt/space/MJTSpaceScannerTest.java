package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MJTSpaceScannerTest {

    private MJTSpaceScanner scanner;

    @Mock
    private Rijndael cipher;

    private List<Mission> missions;
    private List<Rocket> rockets;

    @BeforeEach
    void setUp() {
        missions = new ArrayList<>();
        rockets = new ArrayList<>();

        scanner = new MJTSpaceScanner(missions, rockets, cipher);
    }

    @Test
    void testGetAllMissionsWithEmptyMissionList() {
        assertTrue(scanner.getAllMissions().isEmpty(), "Should return empty list for no missions");
    }

    @Test
    void testGetAllMissionsWithProperData() {
        Mission m1 = mock(Mission.class);
        scanner = new MJTSpaceScanner(List.of(m1), rockets, cipher);

        assertEquals(1, scanner.getAllMissions().size());
        assertTrue(scanner.getAllMissions().contains(m1));
    }

    @Test
    void testGetAllMissionsWithStatusNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getAllMissions(null));
    }

    @Test
    void testGetAllMissionsWithNoMissionsWithThisStatus() {
        Mission m1 = mock(Mission.class);
        when(m1.missionStatus()).thenReturn(MissionStatus.FAILURE);

        scanner = new MJTSpaceScanner(List.of(m1), rockets, cipher);

        assertTrue(scanner.getAllMissions(MissionStatus.SUCCESS).isEmpty());
    }

    @Test
    void testGetAllMissionsWithSuccessStatus() {
        Mission m1 = mock(Mission.class);
        when(m1.missionStatus()).thenReturn(MissionStatus.SUCCESS);
        Mission m2 = mock(Mission.class);
        when(m2.missionStatus()).thenReturn(MissionStatus.FAILURE);

        scanner = new MJTSpaceScanner(List.of(m1, m2), rockets, cipher);

        List<Mission> result = List.copyOf(scanner.getAllMissions(MissionStatus.SUCCESS));
        assertEquals(1, result.size());
        assertEquals(m1, result.getFirst());
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithInvalidTimeFrame() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 1);

        assertThrows(TimeFrameMismatchException.class,
                () -> scanner.getCompanyWithMostSuccessfulMissions(from, to));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsInclusiveFrom() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);

        scanner = new MJTSpaceScanner(List.of(m1), rockets, cipher);

        String company = scanner.getCompanyWithMostSuccessfulMissions(date, date.plusDays(1));
        assertEquals("comp", company);
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsInclusiveTo() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);

        scanner = new MJTSpaceScanner(List.of(m1), rockets, cipher);

        String company = scanner.getCompanyWithMostSuccessfulMissions(date.minusDays(1), date);
        assertEquals("comp", company);
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithNullFrom() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getCompanyWithMostSuccessfulMissions(null, date));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithNullTo() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getCompanyWithMostSuccessfulMissions(date, null));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithNoMissionsInTheTimeFrame() {
        LocalDate date = LocalDate.of(2020, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        scanner = new MJTSpaceScanner(List.of(m1), rockets, cipher);

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        assertEquals("", scanner.getCompanyWithMostSuccessfulMissions(from, to),
                "Should return empty string if no missions found");

    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithNoMissions() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        assertTrue(scanner.getCompanyWithMostSuccessfulMissions(from, to).isEmpty());
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithProperData() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m3 = new Mission("2", "comp2", "loc", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);

        scanner = new MJTSpaceScanner(List.of(m1, m2, m3), rockets, cipher);

        assertEquals("comp",
                scanner.getCompanyWithMostSuccessfulMissions(LocalDate.of(2020, 1, 1), LocalDate.of(2026, 1, 1)));

    }

    @Test
    void testGetMissionsPerCountryWithNoMissions() {
        assertTrue(scanner.getMissionsPerCountry().isEmpty());
    }

    @Test
    void testGetMissionsPerCountryWithProperData() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m3 = new Mission("3", "comp2", "loc, USA", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);

        scanner = new MJTSpaceScanner(List.of(m1, m2, m3), rockets, cipher);
        Map<String, Collection<Mission>> result = scanner.getMissionsPerCountry();

        assertEquals(2, result.get("Bulgaria").size());
        assertEquals(1, result.get("USA").size());
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithZeroN() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(-1, MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithNullRocketStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, null));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithNullMissionStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNLeastExpensiveMissions(1, null, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithNoMissions() {
        assertTrue(scanner.getTopNLeastExpensiveMissions(5, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE).isEmpty());
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithProperData() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(200.0), MissionStatus.SUCCESS);
        Mission m3 = new Mission("3", "comp2", "loc, USA", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(300.0), MissionStatus.SUCCESS);

        scanner = new MJTSpaceScanner(List.of(m1, m2, m3), rockets, cipher);
        List<Mission> result = scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(2, result.size());
        assertEquals(m1, result.get(0), "Cheapest should be first");
        assertEquals(m2, result.get(1), "Second cheapest should be second");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWithNoCost() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);
        Mission m3 = new Mission("3", "comp2", "loc, USA", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);

        scanner = new MJTSpaceScanner(List.of(m1, m2, m3), rockets, cipher);
        List<Mission> result = scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(1, result.size(), "Missions with no cost must emitted");
        assertEquals(m1, result.getFirst(), "Cheapest should be first");
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompanyWithNoMissions() {
        assertTrue(scanner.getMostDesiredLocationForMissionsPerCompany().isEmpty());
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompanyWithProperData() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);
        Mission m3 = new Mission("3", "comp", "loc, USA", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);

        scanner = new MJTSpaceScanner(List.of(m1, m2, m3), rockets, cipher);
        Map<String, String> result = scanner.getMostDesiredLocationForMissionsPerCompany();

        assertEquals("loc, Bulgaria", result.get("comp"));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWithInvalidTimeFrame() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 1);

        assertThrows(TimeFrameMismatchException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWithNullFrom() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(null, date));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWithNullTo() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(date, null));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWithNoMissions() {
        assertTrue(scanner.getLocationWithMostSuccessfulMissionsPerCompany(
                LocalDate.now(), LocalDate.now()).isEmpty());
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWithCompanyWithNoSuccessfulMissions() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.FAILURE);

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        scanner = new MJTSpaceScanner(List.of(m1), rockets, cipher);

        assertTrue(scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to).isEmpty());
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWithProperData() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Detail detail = new Detail("R1", "data");
        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc, USA", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.FAILURE);
        Mission m3 = new Mission("3", "comp1", "loc, USA", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);
        Mission m4 = new Mission("4", "comp", "loc, USA", date,
                detail, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.FAILURE);

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        scanner = new MJTSpaceScanner(List.of(m1, m2, m3, m4), rockets, cipher);
        Map<String, String> result = scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to);

        assertEquals("loc, Bulgaria", result.get("comp"));
        assertEquals("loc, USA", result.get("comp1"));
    }

    @Test
    void testGetAllRocketsWithOneRocket() {
        Rocket r1 = new Rocket("1", "R1", Optional.of("wiki"), Optional.of(10.0));
        scanner = new MJTSpaceScanner(missions, List.of(r1), cipher);
        assertEquals(1, scanner.getAllRockets().size());
    }

    @Test
    void testGetAllRocketsWithNoRockets() {
        assertTrue(scanner.getAllRockets().isEmpty());
    }

    @Test
    void testGetTopNTallestRocketsWithNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNTallestRockets(-1));
    }

    @Test
    void testGetTopNTallestRocketsWithZeroN() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getTopNTallestRockets(0));
    }

    @Test
    void testGetTopNTallestRocketsWithNoRockets() {
        assertTrue(scanner.getTopNTallestRockets(5).isEmpty());
    }

    @Test
    void testGetTopNTallestRocketsWithProperData() {
        Rocket r1 = new Rocket("1", "R1", Optional.of("wiki"), Optional.of(100.0));
        Rocket r2 = new Rocket("2", "R2", Optional.of("wiki"), Optional.of(10.0));
        Rocket r3 = new Rocket("3", "R3", Optional.of("wiki"), Optional.of(1.0));

        scanner = new MJTSpaceScanner(missions, List.of(r1, r2, r3), cipher);
        List<Rocket> result = scanner.getTopNTallestRockets(2);

        assertEquals(2, result.size());
        assertEquals(r1, result.get(0));
        assertEquals(r2, result.get(1));
    }

    @Test
    void testGetTopNTallestRocketsWithNoHeight() {
        Rocket r1 = new Rocket("1", "R1", Optional.of("wiki"), Optional.of(100.0));
        Rocket r2 = new Rocket("2", "R2", Optional.of("wiki"), Optional.empty());

        scanner = new MJTSpaceScanner(missions, List.of(r1, r2), cipher);
        List<Rocket> result = scanner.getTopNTallestRockets(2);

        assertEquals(1, result.size(), "Rockets with no height must be emitted");
        assertEquals(r1, result.getFirst(), "First rocket must be present");
    }

    @Test
    void testGetWikiPageForRocketWithNoRockets() {
        assertTrue(scanner.getWikiPageForRocket().isEmpty());
    }

    @Test
    void testGetWikiPageForRocketWithRocketWithNoWikiPage() {
        Rocket r1 = new Rocket("1", "R1", Optional.empty(), Optional.of(100.0));

        scanner = new MJTSpaceScanner(missions, List.of(r1), cipher);

        Map<String, Optional<String>> result = scanner.getWikiPageForRocket();
        assertEquals(Optional.empty(), result.get("R1"));
    }

    @Test
    void testGetWikiPageForRocketWithRocketWithProperData() {
        Rocket r1 = new Rocket("1", "R1", Optional.of("wiki"), Optional.of(100.0));

        scanner = new MJTSpaceScanner(missions, List.of(r1), cipher);

        assertEquals(Optional.of("wiki"), scanner.getWikiPageForRocket().get("R1"));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNullRocketStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        1, MissionStatus.SUCCESS, null));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNullMissionStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        1, null, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithZeroN() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        -1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNoMissions() {
        assertTrue(scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                5, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE).isEmpty());
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithProperData() {
        Rocket r1 = new Rocket("1", "R1", Optional.of("wiki"), Optional.of(100.0));
        Rocket r2 = new Rocket("2", "R2", Optional.of("wiki1"), Optional.of(10.0));
        Rocket r3 = new Rocket("3", "R3", Optional.of("wiki2"), Optional.of(1.0));

        LocalDate date = LocalDate.of(2025, 1, 1);
        Detail detail1 = new Detail("R1", "data");
        Detail detail2 = new Detail("R2", "data");

        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail1, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc, USA", date,
                detail2, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.FAILURE);
        Mission m3 = new Mission("3", "comp1", "loc, USA", date,
                detail2, RocketStatus.STATUS_ACTIVE, Optional.of(10.0), MissionStatus.SUCCESS);


        scanner = new MJTSpaceScanner(List.of(m1, m2, m3), List.of(r1, r2, r3), cipher);
        List<String> wikis = scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(1, wikis.size());
        assertEquals("wiki", wikis.getFirst());
    }

    @Test
    void testSaveMostReliableRocketWithInvalidTimeFrame() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 1);

        OutputStream outputStream = mock();

        assertThrows(TimeFrameMismatchException.class,
                () -> scanner.saveMostReliableRocket(outputStream, from, to));
    }

    @Test
    void testSaveMostReliableRocketWithNullFrom() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        OutputStream outputStream = mock();

        assertThrows(IllegalArgumentException.class,
                () -> scanner.saveMostReliableRocket(outputStream, null, date));
    }

    @Test
    void testSaveMostReliableRocketWithNullTo() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        OutputStream outputStream = mock();

        assertThrows(IllegalArgumentException.class,
                () -> scanner.saveMostReliableRocket(outputStream, date, null));
    }

    @Test
    void testSaveMostReliableRocketWithNullOutputStream() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> scanner.saveMostReliableRocket(null, from, to));
    }

    @Test
    void testSaveMostReliableRocketWithNoMissions() throws CipherException {
        OutputStream outputStream = mock();

        scanner.saveMostReliableRocket(outputStream, LocalDate.now(), LocalDate.now());

        verify(cipher, Mockito.times(0)).encrypt(any(), any());
    }

    @Test
    void testSaveMostReliableRocketWithProperData() throws CipherException {
        Rocket r1 = new Rocket("1", "R1", Optional.of("wiki"), Optional.of(100.0));
        Rocket r2 = new Rocket("2", "R2", Optional.of("wiki1"), Optional.of(10.0));

        LocalDate date = LocalDate.of(2025, 1, 1);
        Detail detail1 = new Detail("R1", "data");
        Detail detail2 = new Detail("R2", "data");

        Mission m1 = new Mission("1", "comp", "loc, Bulgaria", date,
                detail1, RocketStatus.STATUS_ACTIVE, Optional.of(100.0), MissionStatus.SUCCESS);
        Mission m2 = new Mission("2", "comp", "loc, USA", date,
                detail2, RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.FAILURE);

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        scanner = new MJTSpaceScanner(List.of(m1, m2), List.of(r1, r2), cipher);
        OutputStream outputStream = mock();

        scanner.saveMostReliableRocket(
                outputStream, from, to);

        verify(cipher).encrypt(any(), any());
    }
}