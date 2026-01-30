package bg.sofia.uni.fmi.mjt.authserver.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Session {

    private static final int SESSION_TTL_IN_MINUTES = 60;
    private final String id;
    private final String userId;
    private final LocalDateTime timeToLive;

    public Session(String userId) {
        this.id = generateId();
        this.userId = userId;
        timeToLive = LocalDateTime.now().plusMinutes(SESSION_TTL_IN_MINUTES);
    }

    public boolean isExpired() {
        return !LocalDateTime.now().isBefore(timeToLive);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
