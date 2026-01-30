package bg.sofia.uni.fmi.mjt.authserver.entity;

import java.nio.Buffer;
import java.time.LocalDateTime;

public class ConnectionData {

    private static final int ALLOWED_UNSUCCESSFUL_LOGINS = 10;
    private static final long BANNED_TIME_IN_MINUTES = 10;

    private int unsuccessfulLoginCount = 0;
    private LocalDateTime bannedUntil;
    private String sessionId;
    private String userId;

    private final Buffer buffer;

    public ConnectionData(Buffer buffer) {
        this.buffer = buffer;
    }

    public boolean isBanned() {
        return bannedUntil != null && LocalDateTime.now().isBefore(bannedUntil);
    }

    public boolean isLogged() {
        return userId != null;
    }

    public void clearConnectionData() {
        sessionId = null;
        userId = null;
    }

    public void registerSuccessfulLogin(String sessionId, String userId) {
        this.userId = userId;
        this.sessionId = sessionId;
        unsuccessfulLoginCount = 0;
        bannedUntil = null;
    }

    public void registerUnsuccessfulLogin() {
        unsuccessfulLoginCount++;
        if (unsuccessfulLoginCount >= ALLOWED_UNSUCCESSFUL_LOGINS) {
            bannedUntil = LocalDateTime.now().plusMinutes(BANNED_TIME_IN_MINUTES);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public Buffer getBuffer() {
        return buffer;
    }
}
