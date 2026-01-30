package bg.sofia.uni.fmi.mjt.authserver.manager;

import bg.sofia.uni.fmi.mjt.authserver.entity.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    void testStartSessionReturnsNewSession() {
        Session session = sessionManager.startSession("user1");
        assertNotNull(session, "startSession should return a non-null session");
    }

    @Test
    void testStartSessionCreatesSessionWithCorrectUserId() {
        Session session = sessionManager.startSession("user1");
        assertEquals("user1", session.getUserId(), "startSession should create session with correct userId");
    }

    @Test
    void testIsSessionActiveReturnsTrueForActiveSession() {
        Session session = sessionManager.startSession("user1");
        assertTrue(sessionManager.isSessionActive(session.getId()),
                "isSessionActive should return true for a newly created session");
    }

    @Test
    void testIsSessionActiveReturnsFalseForNonExistentSession() {
        assertFalse(sessionManager.isSessionActive("nonExistentId"),
                "isSessionActive should return false for a non-existent session");
    }

    @Test
    void testHasSessionReturnsTrueForUserWithSession() {
        sessionManager.startSession("user1");
        assertTrue(sessionManager.hasSession("user1"),
                "hasSession should return true for a user with an active session");
    }

    @Test
    void testHasSessionReturnsFalseForUserWithoutSession() {
        assertFalse(sessionManager.hasSession("unknownUser"),
                "hasSession should return false for a user without a session");
    }

    @Test
    void testGetSessionReturnsCorrectSession() {
        Session session = sessionManager.startSession("user1");
        Session retrieved = sessionManager.getSession(session.getId());
        assertEquals(session, retrieved, "getSession should return the same session that was started");
    }

    @Test
    void testGetSessionReturnsNullForNonExistentSession() {
        Session result = sessionManager.getSession("nonExistentId");
        assertNull(result, "getSession should return null for a non-existent session id");
    }

    @Test
    void testEndSessionWithSessionIdRemovesSession() {
        Session session = sessionManager.startSession("user1");
        sessionManager.endSessionWithSessionId(session.getId());
        assertFalse(sessionManager.isSessionActive(session.getId()),
                "isSessionActive should return false after ending the session by session id");
    }

    @Test
    void testEndSessionWithUserIdRemovesSession() {
        Session session = sessionManager.startSession("user1");
        sessionManager.endSessionWithUserId("user1");
        assertFalse(sessionManager.isSessionActive(session.getId()),
                "isSessionActive should return false after ending the session by user id");
    }

    @Test
    void testStartSessionInvalidatesPreviousSession() {
        Session firstSession = sessionManager.startSession("user1");
        Session secondSession = sessionManager.startSession("user1");
        assertFalse(sessionManager.isSessionActive(firstSession.getId()),
                "first session should be inactive after starting a new session for the same user");
        assertTrue(sessionManager.isSessionActive(secondSession.getId()),
                "second session should be active after starting a new session for the same user");
    }

    @Test
    void testEndSessionWithSessionIdDoesNothingForUnknownId() {
        sessionManager.endSessionWithSessionId("unknownId");
        assertFalse(sessionManager.isSessionActive("unknownId"),
                "endSessionWithSessionId should not throw for unknown session id");
    }

    @Test
    void testEndSessionWithUserIdDoesNothingForUnknownUser() {
        sessionManager.endSessionWithUserId("unknownUser");
        assertFalse(sessionManager.hasSession("unknownUser"),
                "endSessionWithUserId should not throw for unknown user id");
    }
}