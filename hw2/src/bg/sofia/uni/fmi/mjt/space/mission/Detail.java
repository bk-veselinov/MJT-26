package bg.sofia.uni.fmi.mjt.space.mission;

import java.util.Objects;

public record Detail(String rocketName, String payload) {
    private static final int ROCKET_NAME_IDX = 0;
    private static final int PAYLOAD_IDX = 1;

    public static Detail of(String stringData) {
        String[] splitLine = stringData.split("\\|");

        String rocketName = splitLine[ROCKET_NAME_IDX].strip().replace("\"", "");
        String payload = splitLine[PAYLOAD_IDX].strip();

        return new Detail(rocketName, payload);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Detail detail = (Detail) o;
        return Objects.equals(payload, detail.payload) && Objects.equals(rocketName, detail.rocketName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rocketName, payload);
    }
}