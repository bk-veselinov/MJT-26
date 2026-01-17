package bg.sofia.uni.fmi.mjt.newsfeed.entity;

public enum Category {
    BUSINESS,
    ENTERTAINMENT,
    GENERAL,
    HEALTH,
    SCIENCE,
    SPORTS,
    TECHNOLOGY;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
