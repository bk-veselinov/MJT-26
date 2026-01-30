package bg.sofia.uni.fmi.mjt.authserver.manager;

import bg.sofia.uni.fmi.mjt.authserver.cryptography.Cryptography;
import bg.sofia.uni.fmi.mjt.authserver.entity.Session;
import bg.sofia.uni.fmi.mjt.authserver.entity.User;
import bg.sofia.uni.fmi.mjt.authserver.entity.UserRole;
import bg.sofia.uni.fmi.mjt.authserver.exception.InactiveSessionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUserException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.authserver.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthenticationManagerTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private UserDBManager userDBManager;

    @Mock
    private Cryptography cryptography;

    private AuthenticationManager authManager;

    private static final String SESSION_ID = "session-123";
    private static final String USER_ID = "user-456";

    @BeforeEach
    void setUp() {
        authManager = new AuthenticationManager(sessionManager, userDBManager, cryptography);
    }

    @Test
    void testRegisterUserSuccessfully() {
        when(userDBManager.isUsernameUsed("newUser")).thenReturn(false);
        when(cryptography.encrypt("pass")).thenReturn("encryptedPass");
        when(userDBManager.getAdminCount()).thenReturn(1);
        when(sessionManager.startSession(anyString())).thenReturn(new Session("userId"));

        Session session = authManager.registerUser("newUser", "pass", "fName", "lName", "fName@test.com");
        assertNotNull(session, "registerUser should return a non-null session");
    }

    @Test
    void testRegisterUserThrowsForExistingUsername() {
        when(userDBManager.isUsernameUsed("existing")).thenReturn(true);

        assertThrows(InvalidUsernameException.class,
                () -> authManager.registerUser("existing", "pass", "fName", "lName", "fName@test.com"),
                "registerUser should throw InvalidUsernameException for existing username");
    }

    @Test
    void testRegisterUserFirstUserGetsAdminRole() {
        when(userDBManager.isUsernameUsed("firstUser")).thenReturn(false);
        when(cryptography.encrypt("pass")).thenReturn("encryptedPass");
        when(userDBManager.getAdminCount()).thenReturn(0);
        when(sessionManager.startSession(anyString())).thenReturn(new Session("userId"));

        authManager.registerUser("firstUser", "pass", "fName", "lName", "fName@test.com");

        verify(userDBManager).addUser(any(User.class));
    }

    @Test
    void testLoginUserSuccessfully() {
        User user = new User("testUser", "encryptedPass", "fName", "lName", "fName@test.com", UserRole.NORMAL_USER);
        when(userDBManager.getUserByUsername("testUser")).thenReturn(user);
        when(cryptography.decrypt("encryptedPass")).thenReturn("rawPass");
        when(sessionManager.startSession(user.getId())).thenReturn(new Session(user.getId()));

        Session session = authManager.loginUser("testUser", "rawPass");
        assertNotNull(session, "loginUser should return a non-null session");
    }

    @Test
    void testLoginUserThrowsForWrongPassword() {
        User user = new User("testUser", "encryptedPass", "fName", "lName", "fName@test.com", UserRole.NORMAL_USER);
        when(userDBManager.getUserByUsername("testUser")).thenReturn(user);
        when(cryptography.decrypt("encryptedPass")).thenReturn("correctPass");

        assertThrows(InvalidPasswordException.class,
                () -> authManager.loginUser("testUser", "wrongPass"),
                "loginUser should throw InvalidPasswordException for wrong password");
    }

    @Test
    void testLoginUserThrowsForNullUser() {
        when(userDBManager.getUserByUsername("unknown")).thenReturn(null);

        assertThrows(InvalidUserException.class,
                () -> authManager.loginUser("unknown", "pass"),
                "loginUser should throw InvalidUserException for non-existent user");
    }

    @Test
    void testLoginUserWithSessionIdSuccessfully() {
        Session session = new Session(USER_ID);
        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);

        Session result = authManager.loginUserWithSessionId(SESSION_ID);
        assertNotNull(result, "loginUserWithSessionId should return a non-null session");
    }

    @Test
    void testLoginUserWithSessionIdThrowsForInactiveSession() {
        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(false);

        assertThrows(InactiveSessionException.class,
                () -> authManager.loginUserWithSessionId(SESSION_ID),
                "loginUserWithSessionId should throw InactiveSessionException for inactive session");
    }

    @Test
    void testUpdateUserDataSuccessfully() {
        Session session = new Session(USER_ID);
        User user = new User("testUser", "pass", "fName", "lName", "fName@test.com", UserRole.NORMAL_USER);
        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(user);

        authManager.updateUserData(SESSION_ID, "newName", null, null, null);

        verify(userDBManager).updateUserData(eq(USER_ID), any(User.class));
    }

    @Test
    void testUpdateUserDataThrowsForInactiveSession() {
        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(false);

        assertThrows(InactiveSessionException.class,
                () -> authManager.updateUserData(SESSION_ID, "newName", null, null, null),
                "updateUserData should throw InactiveSessionException for inactive session");
    }

    @Test
    void testChangePasswordSuccessfully() {
        Session session = new Session(USER_ID);
        User user = new User("testUser", "encOld", "fName", "lName", "fName@test.com", UserRole.NORMAL_USER);
        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(user);
        when(cryptography.decrypt("encOld")).thenReturn("oldPass");
        when(cryptography.encrypt("newPass")).thenReturn("encNew");

        authManager.changePassword(SESSION_ID, "testUser", "oldPass", "newPass");

        verify(userDBManager).updateUserData(eq(USER_ID), any(User.class));
    }

    @Test
    void testChangePasswordThrowsForWrongOldPassword() {
        Session session = new Session(USER_ID);
        User user = new User("testUser", "encOld", "fName", "lName", "fName@test.com", UserRole.NORMAL_USER);
        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(user);
        when(cryptography.decrypt("encOld")).thenReturn("correctOldPass");

        assertThrows(InvalidPasswordException.class,
                () -> authManager.changePassword(SESSION_ID, "testUser", "wrongOldPass", "newPass"),
                "changePassword should throw InvalidPasswordException for wrong old password");
    }

    @Test
    void testChangePasswordThrowsForWrongUsername() {
        Session session = new Session(USER_ID);
        User user = new User("testUser", "encOld", "fName", "lName", "fName@test.com", UserRole.NORMAL_USER);
        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(user);

        assertThrows(InvalidUsernameException.class,
                () -> authManager.changePassword(SESSION_ID, "wrongUser", "old", "new"),
                "changePassword should throw InvalidUsernameException when username lNames not match");
    }

    @Test
    void testLogoutUserEndsSessions() {
        authManager.logoutUser(SESSION_ID);
        verify(sessionManager).endSessionWithSessionId(SESSION_ID);
    }

    @Test
    void testAddAdminRoleSuccessfully() {
        Session session = new Session(USER_ID);
        User adminUser = new User("admin", "pass", "Admin", "User", "a@t.com", UserRole.ADMIN);
        User targetUser = new User("target", "pass", "Target", "User", "t@t.com", UserRole.NORMAL_USER);

        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(adminUser);
        when(userDBManager.isUsernameUsed("target")).thenReturn(true);
        when(userDBManager.getUserByUsername("target")).thenReturn(targetUser);

        authManager.addAdminRole(SESSION_ID, "target");

        verify(userDBManager).addAdminRole(any(User.class));
    }

    @Test
    void testAddAdminRoleThrowsForNonAdminUser() {
        Session session = new Session(USER_ID);
        User normalUser = new User("normal", "pass", "Normal", "User", "n@t.com", UserRole.NORMAL_USER);

        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(normalUser);

        assertThrows(UnauthorizedAccessException.class,
                () -> authManager.addAdminRole(SESSION_ID, "target"),
                "addAdminRole should throw UnauthorizedAccessException for non-admin users");
    }

    @Test
    void testRemoveAdminRoleSuccessfully() {
        Session session = new Session(USER_ID);
        User adminUser = new User("admin", "pass", "Admin", "User", "a@t.com", UserRole.ADMIN);
        User targetUser = new User("target", "pass", "Target", "User", "t@t.com", UserRole.ADMIN);

        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(adminUser);
        when(userDBManager.isUsernameUsed("target")).thenReturn(true);
        when(userDBManager.getAdminCount()).thenReturn(2);
        when(userDBManager.getUserByUsername("target")).thenReturn(targetUser);

        authManager.removeAdminRole(SESSION_ID, "target");

        verify(userDBManager).removeAdminRole(any(User.class));
    }

    @Test
    void testRemoveAdminRoleThrowsWhenOnlyOneAdmin() {
        Session session = new Session(USER_ID);
        User adminUser = new User("admin", "pass", "Admin", "User", "a@t.com", UserRole.ADMIN);

        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(adminUser);
        when(userDBManager.isUsernameUsed("target")).thenReturn(true);
        when(userDBManager.getAdminCount()).thenReturn(1);

        assertThrows(InvalidActionException.class,
                () -> authManager.removeAdminRole(SESSION_ID, "target"),
                "removeAdminRole should throw InvalidActionException when only one admin exists");
    }

    @Test
    void testRemoveUserSuccessfully() {
        Session session = new Session(USER_ID);
        User adminUser = new User("admin", "pass", "Admin", "User", "a@t.com", UserRole.ADMIN);
        User targetUser = new User("target", "pass", "Target", "User", "t@t.com", UserRole.NORMAL_USER);

        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserByUsername("target")).thenReturn(targetUser);
        when(userDBManager.getUserById(USER_ID)).thenReturn(adminUser);
        when(userDBManager.isUsernameUsed("target")).thenReturn(true);
        when(userDBManager.removeUser("target")).thenReturn(targetUser);

        authManager.removeUser(SESSION_ID, "target");

        verify(userDBManager).removeUser("target");
        verify(sessionManager).endSessionWithUserId(targetUser.getId());
    }

    @Test
    void testRemoveUserThrowsForNonExistentUsername() {
        Session session = new Session(USER_ID);
        User adminUser = new User("admin", "pass", "Admin", "User", "a@t.com", UserRole.ADMIN);

        when(sessionManager.isSessionActive(SESSION_ID)).thenReturn(true);
        when(sessionManager.getSession(SESSION_ID)).thenReturn(session);
        when(userDBManager.getUserById(USER_ID)).thenReturn(adminUser);
        when(userDBManager.isUsernameUsed("nonExistent")).thenReturn(false);

        assertThrows(InvalidUsernameException.class,
                () -> authManager.removeUser(SESSION_ID, "nonExistent"),
                "removeUser should throw InvalidUsernameException for non-existent username");
    }
}
