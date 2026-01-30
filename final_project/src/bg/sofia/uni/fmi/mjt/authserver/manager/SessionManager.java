package bg.sofia.uni.fmi.mjt.authserver.manager;

import bg.sofia.uni.fmi.mjt.authserver.entity.Session;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final ConcurrentHashMap<String, Session> sessionIdToSession;
    private final ConcurrentHashMap<String, Session> userIdToSession;

    public SessionManager() {
        sessionIdToSession = new ConcurrentHashMap<>();
        userIdToSession = new ConcurrentHashMap<>();
    }

    public Session startSession(String userId) {
        if (hasSession(userId)) {
            removeSession(userIdToSession.get(userId));
        }

        Session session = new Session(userId);
        sessionIdToSession.put(session.getId(), session);
        userIdToSession.put(userId, session);
        return sessionIdToSession.get(session.getId());
    }

    public void endSessionWithUserId(String userId) {
        if (userIdToSession.containsKey(userId)) {
            removeSession(userIdToSession.get(userId));
        }
    }

    public void endSessionWithSessionId(String sessionId) {
        if (sessionIdToSession.containsKey(sessionId)) {
            removeSession(sessionIdToSession.get(sessionId));
        }
    }

    public boolean hasSession(String userId) {
        if (!userIdToSession.containsKey(userId)) {
            return false;
        }
        return isSessionActive(userIdToSession.get(userId).getId());
    }

    public boolean isSessionActive(String sessionId) {
        if (!sessionIdToSession.containsKey(sessionId)) {
            return false;
        }

        Session session = sessionIdToSession.get(sessionId);

        if (session.isExpired()) {
            removeSession(session);
            return false;
        }

        return true;
    }

    public Session getSession(String sessionId) {
        return sessionIdToSession.get(sessionId);
    }

    private void removeSession(Session session) {
        sessionIdToSession.remove(session.getId());
        userIdToSession.remove(session.getUserId());
    }

}
