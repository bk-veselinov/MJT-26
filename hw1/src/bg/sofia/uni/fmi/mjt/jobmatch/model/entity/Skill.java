package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.Objects;

public record Skill(String name, int level) {

    private static final int MAX_LEVEL = 5;
    private static final int MIN_LEVEL = 0;

    public Skill {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        } else if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }

        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new IllegalArgumentException("level must be a number between [0, 5]");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(name, skill.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
