package bg.sofia.uni.fmi.mjt.show.date;

public class DateEvent {
    private static final int TENSION_LEVEL_MIN = 0;
    private static final int TENSION_LEVEL_MAX = 10;

    private final String location;
    private final int tensionLevel;
    private final int duration;

    public DateEvent(String location, int tensionLevel, int duration) {
        this.location = location;
        this.duration = duration;
        if (tensionLevel < TENSION_LEVEL_MIN) {
            this.tensionLevel = TENSION_LEVEL_MIN;
        } else {
            this.tensionLevel = Math.min(tensionLevel, TENSION_LEVEL_MAX);
        }
    }

    public String getLocation() {
        return location;
    }

    public int getTensionLevel() {
        return tensionLevel;
    }

    public int getDuration() {
        return duration;
    }
}