import bg.sofia.uni.fmi.mjt.space.MJTSpaceScanner;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;

public static class Test {
    final int KILOBYTE = 1024;
    final String ENCRYPTION_ALGORITHM = "AES"; // //  Advanced Encryption Standard
    final String KEY_FILE_PATH = "secret.key";
    final int KEY_SIZE_IN_BITS = 128; // Key sizes like 192 or 256 might not be available on all systems


    SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);
        SecretKey secretKey = keyGenerator.generateKey();

        // In order to view the key in some text representation, we'll convert it to Base64 format
        // Comment the next two lines if that's not needed
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Secret Key (Base64-encoded): " + base64Key);

        return secretKey;
    }
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or

// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

void main() throws FileNotFoundException, NoSuchAlgorithmException {
    Test test = new Test();

    double m = Double.valueOf("5000.00");
    Reader missionReader = new FileReader("missions.csv");
    Reader rocketReader = new FileReader("rockets.csv");
    MJTSpaceScanner scanner = new MJTSpaceScanner(missionReader, rocketReader, test.generateSecretKey());

    LocalDate from = LocalDate.of(1900, 1, 1);
    LocalDate to = LocalDate.of(2025, 1, 1);

    print(scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(5, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    print(scanner.getWikiPageForRocket());
    print(scanner.getAllMissions());
    print(scanner.getAllMissions(MissionStatus.SUCCESS));
    print(scanner.getCompanyWithMostSuccessfulMissions(from, to));
    print(scanner.getTopNLeastExpensiveMissions(3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));
    print(scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to));
    print(scanner.getAllRockets());
    print(scanner.getTopNTallestRockets(5));
    print(scanner.getMissionsPerCountry());
    print(scanner.getMostDesiredLocationForMissionsPerCompany());

}

private static void print(Object result) {
    // If the result is a Collection (List, Set) or Map, this prints it nicely
    if (result instanceof Iterable) {
        ((Iterable<?>) result).forEach(System.out::println);
    } else {
        System.out.println(result);
    }
    System.out.println("----------------------");
}
