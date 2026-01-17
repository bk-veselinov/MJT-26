package bg.sofia.uni.fmi.mjt.newsfeed.entity;

import java.util.Objects;

public record Source(String id, String name) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return Objects.equals(id, source.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
