package bg.sofia.uni.fmi.mjt.space.mission;

public enum MissionStatus {
    SUCCESS("Success"),
    FAILURE("Failure"),
    PARTIAL_FAILURE("Partial Failure"),
    PRELAUNCH_FAILURE("Prelaunch Failure");

    private final String value;

    MissionStatus(String value) {
        this.value = value;
    }

    public static MissionStatus ofValue(String value) {
        for (MissionStatus missionStatus : MissionStatus.values()) {
            if (value.equalsIgnoreCase(missionStatus.value)) {
                return missionStatus;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid Mission Status %s", value));
    }

    @Override
    public String toString() {
        return value;
    }
}