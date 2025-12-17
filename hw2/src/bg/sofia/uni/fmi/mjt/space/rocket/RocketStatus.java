package bg.sofia.uni.fmi.mjt.space.rocket;

public enum RocketStatus {
    STATUS_RETIRED("StatusRetired"),
    STATUS_ACTIVE("StatusActive");

    private final String value;

    RocketStatus(String value) {
        this.value = value;
    }

    public static RocketStatus ofValue(String value) {
        for (RocketStatus rocketStatus : RocketStatus.values()) {
            if (value.equalsIgnoreCase(rocketStatus.value)) {
                return rocketStatus;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid Rocket Status %s", value));
    }

    @Override
    public String toString() {
        return value;
    }
}