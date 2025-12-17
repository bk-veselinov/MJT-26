package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public record Mission(String id,
                      String company,
                      String location,
                      LocalDate date,
                      Detail detail,
                      RocketStatus rocketStatus,
                      Optional<Double> cost,
                      MissionStatus missionStatus) {

    private static final int ID_IDX = 0;
    private static final int COMPANY_IDX = 1;
    private static final int LOCATION_IDX = 2;
    private static final int DATE_IDX = 3;
    private static final int DETAIL_IDX = 4;
    private static final int ROCKET_STATUS_IDX = 5;
    private static final int COST_IDX = 6;
    private static final int MISSION_STATUS_IDX = 7;

    private static LocalDate parseDate(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("\"EEE MMM dd, yyyy");
        return LocalDate.parse(dateAsString, formatter);
    }

    public String getCountry() {
        String[] splitLocation = location.split(",");
        return splitLocation[splitLocation.length - 1].strip();
    }

    public static Mission of(String stringData) {

        stringData = stringData.replace(",,", ",_,");
        String[] splitData = stringData.split("(,\")|(\",)|(,(?!\\d)\\b)");

        String id = splitData[ID_IDX];
        String company = splitData[COMPANY_IDX];
        String location = splitData[LOCATION_IDX];
        LocalDate date = parseDate(splitData[DATE_IDX]);
        Detail detail = Detail.of(splitData[DETAIL_IDX]);

        RocketStatus rocketStatus = RocketStatus.ofValue(splitData[ROCKET_STATUS_IDX]);

        Optional<Double> cost = splitData[COST_IDX].equals("_") ?
                Optional.empty() : Optional.of(Double.valueOf(
                splitData[COST_IDX].replace(",", "")));

        MissionStatus missionStatus = MissionStatus.ofValue(splitData[MISSION_STATUS_IDX]);

        return new Mission(id, company, location, date, detail, rocketStatus, cost, missionStatus);

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(id, mission.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}