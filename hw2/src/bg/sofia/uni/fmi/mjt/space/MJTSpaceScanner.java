package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.loader.Loader;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final Rijndael encryptor;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        nullValidator(missionsReader);
        nullValidator(rocketsReader);
        nullValidator(secretKey);

        missions = Loader.loadObjFromCSV(missionsReader, Mission::of);
        rockets = Loader.loadObjFromCSV(rocketsReader, Rocket::of);
        encryptor = new Rijndael(secretKey);
    }

    MJTSpaceScanner(List<Mission> missions, List<Rocket> rockets, Rijndael encryptor) {
        this.missions = missions;
        this.rockets = rockets;
        this.encryptor = encryptor;
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return List.copyOf(missions);
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        nullValidator(missionStatus);

        return missions.stream()
                .filter(m -> m.missionStatus().equals(missionStatus))
                .toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        timeFrameValidator(from, to);

        Map<String, Long> successfulMissionsByCompany = getMissionsInTimeFrame(from, to)
                .filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS))
                .collect(Collectors.groupingBy(Mission::company, Collectors.counting()));

        return successfulMissionsByCompany.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        Map<String, List<Mission>> missionsByCountry = missions.stream()
                .collect(Collectors.groupingBy(Mission::getCountry));

        return missionsByCountry.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus,
                                                       RocketStatus rocketStatus) {
        nValidator(n);
        nullValidator(missionStatus);
        nullValidator(rocketStatus);

        return getMissionsWithCostMissionStatusAndRocketStatus(missionStatus, rocketStatus)
                .map(m -> Map.entry(m, m.cost())).sorted(
                        Comparator.comparingDouble(m -> m.getValue().get()))
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        Map<String, List<Mission>> missionsByCompany = missions.stream()
                .collect(Collectors.groupingBy(Mission::company));

        return getMostCommonLocationByCompany(missionsByCompany);
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from,
                                                                               LocalDate to) {
        timeFrameValidator(from, to);

        Map<String, List<Mission>> successfulMissionsByCompany = getMissionsInTimeFrame(from, to)
                .filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS))
                .collect(Collectors.groupingBy(Mission::company));

        return getMostCommonLocationByCompany(successfulMissionsByCompany);
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return List.copyOf(rockets);
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        nValidator(n);

        return rockets.stream()
                .filter(r -> r.height().isPresent())
                .sorted(Comparator.comparingDouble((Rocket r) -> r.height().get()).reversed())
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream()
                .collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        nValidator(n);
        nullValidator(missionStatus);
        nullValidator(rocketStatus);

        Set<String> rocketsInTopNMostExpensiveMissions =
                getMissionsWithCostMissionStatusAndRocketStatus(missionStatus, rocketStatus)
                        .map(m -> Map.entry(
                                m,
                                m.cost())).sorted(
                                Comparator.comparingDouble(
                                                (Map.Entry<Mission, Optional<Double>> m) -> m.getValue().get())
                                        .reversed())
                        .limit(n)
                        .map(m -> m.getKey().detail().rocketName())
                        .collect(Collectors.toSet());

        return rockets.stream()
                .filter(r -> rocketsInTopNMostExpensiveMissions.contains(r.name())
                        && r.wiki().isPresent())
                .map(r -> r.wiki().get()).toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream,
                                       LocalDate from, LocalDate to) throws CipherException {
        timeFrameValidator(from, to);
        nullValidator(outputStream);

        Rocket mostReliableRocket = rockets.stream()
                .max(Comparator.comparingDouble(r -> getReliabilityFactor(r.name(), from, to)))
                .orElse(null);

        if (mostReliableRocket == null) {
            return;
        }

        String rocketName = mostReliableRocket.name();

        try (outputStream;
             InputStream dataStream = new ByteArrayInputStream(
                     rocketName.getBytes(StandardCharsets.UTF_8))) {

            encryptor.encrypt(dataStream, outputStream);

        } catch (IOException e) {
            throw new CipherException("Problem during encryption", e);
        }
    }

    private Double getReliabilityFactor(String rocketName, LocalDate from, LocalDate to) {
        List<Mission> missionsOfRocket = getMissionsInTimeFrame(from, to)
                .filter(m -> m.detail().rocketName().equals(rocketName))
                .toList();

        if (missionsOfRocket.isEmpty()) {
            return 0.0;
        }
        long successfulMissionsCount = missionsOfRocket.stream()
                .filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS)).count();
        long failedMissionsCount = missionsOfRocket.size() - successfulMissionsCount;

        return (double) ((2 * successfulMissionsCount + failedMissionsCount) / (2L * missionsOfRocket.size()));
    }

    private static <T> void nullValidator(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Null was passed as an argument");
        }
    }

    private static void timeFrameValidator(LocalDate from, LocalDate to) {
        nullValidator(from);
        nullValidator(to);

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("The given timeframe is invalid(<from> must be before <to>)");
        }
    }

    private static void nValidator(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be > 0");
        }
    }

    private Stream<Mission> getMissionsInTimeFrame(LocalDate from, LocalDate to) {
        return missions.stream()
                .filter(t -> !t.date().isBefore(from) && !t.date().isAfter(to));
    }

    private Stream<Mission> getMissionsWithCostMissionStatusAndRocketStatus(MissionStatus missionStatus,
                                                                            RocketStatus rocketStatus) {
        return missions.stream().filter(m -> m.cost().isPresent()
                && m.missionStatus().equals(missionStatus)
                && m.rocketStatus().equals(rocketStatus));
    }

    private Map<String, Map<String, Long>> getMissionsByCompanyByLocation(
            Map<String, List<Mission>> missionsByCompany) {
        return missionsByCompany.entrySet().stream()
                .map(mc -> Map.entry(
                        mc.getKey(),
                        mc.getValue().stream()
                                .collect(Collectors.groupingBy(Mission::location, Collectors.counting()))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, String> getMostCommonLocationByCompany(Map<String, List<Mission>> missionsByCompany) {
        return getMissionsByCompanyByLocation(missionsByCompany).entrySet().stream()
                .map(mc -> Map.entry(
                        mc.getKey(),
                        mc.getValue().entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .filter(m -> m.getValue() > 0)
                                .map(Map.Entry::getKey).orElse("")
                )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
