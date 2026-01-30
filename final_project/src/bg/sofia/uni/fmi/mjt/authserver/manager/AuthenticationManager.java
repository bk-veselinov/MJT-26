package bg.sofia.uni.fmi.mjt.authserver.manager;

import bg.sofia.uni.fmi.mjt.authserver.cryptography.Cryptography;
import bg.sofia.uni.fmi.mjt.authserver.cryptography.Rijndael;
import bg.sofia.uni.fmi.mjt.authserver.entity.UserRole;
import bg.sofia.uni.fmi.mjt.authserver.entity.Session;
import bg.sofia.uni.fmi.mjt.authserver.entity.User;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUserException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InactiveSessionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.UnauthorizedAccessException;

import javax.crypto.SecretKey;

public class AuthenticationManager {

    private final SessionManager sessionManager;
    private final UserDBManager userDBManager;
    private final Cryptography cryptography;

    public AuthenticationManager(SecretKey secretKey, String dbFileName) {
        this.sessionManager = new SessionManager();
        this.userDBManager = new UserDBManager(dbFileName);
        this.cryptography = new Rijndael(secretKey);
    }

    AuthenticationManager(SessionManager sessionManager, UserDBManager userDBManager, Cryptography cryptography) {
        this.sessionManager = sessionManager;
        this.userDBManager = userDBManager;
        this.cryptography = cryptography;
    }

    public Session registerUser(String username, String password, String firstName, String lastName, String email) {
        if (userDBManager.isUsernameUsed(username)) {
            throw new InvalidUsernameException("Username:" + username + " is already used.");
        }

        User user = createUser(username, password, firstName, lastName, email);
        userDBManager.addUser(user);
        return sessionManager.startSession(user.getId());
    }

    public Session loginUserWithSessionId(String sessionId) {
        validateSession(sessionId);
        return sessionManager.getSession(sessionId);
    }

    public Session loginUser(String username, String password) {
        User user = userDBManager.getUserByUsername(username);

        validateUser(user);
        validatePassword(user.getPassword(), password);

        sessionManager.endSessionWithUserId(user.getId());
        return sessionManager.startSession(user.getId());
    }

    public void updateUserData(String sessionId, String username, String firstName, String lastName, String email) {
        validateSession(sessionId);

        String userId = sessionManager.getSession(sessionId).getUserId();
        User user = userDBManager.getUserById(userId);
        validateUser(user);

        User updatedUser = user
                .updateUser()
                .updateUsername(username)
                .updateFirstName(firstName)
                .updateLastName(lastName)
                .updateEmail(email)
                .update();

        userDBManager.updateUserData(userId, updatedUser);
    }

    public void changePassword(String sessionId, String username, String oldPassword, String newPassword) {
        validateSession(sessionId);

        String userId = sessionManager.getSession(sessionId).getUserId();
        User user = userDBManager.getUserById(userId);
        validateUser(user);

        if (!user.getUserName().equals(username)) {
            throw new InvalidUsernameException("Username:" + username + " does not match the login credential.");
        }

        validatePassword(user.getPassword(), oldPassword);

        User updatedUser = user
                .updateUser()
                .updatePassword(cryptography.encrypt(newPassword))
                .update();

        userDBManager.updateUserData(userId, updatedUser);

    }

    public void logoutUser(String sessionId) {
        sessionManager.endSessionWithSessionId(sessionId);
    }

    public void addAdminRole(String sessionId, String username) {
        validateSession(sessionId);
        verifyAdminUserBySessionId(sessionId);
        validateUsername(username);

        User updatedUser = userDBManager.getUserByUsername(username)
                .updateUser()
                .userUpdaterRole(UserRole.ADMIN)
                .update();

        userDBManager.addAdminRole(updatedUser);
    }

    public void removeAdminRole(String sessionId, String username) {
        validateSession(sessionId);
        verifyAdminUserBySessionId(sessionId);
        validateUsername(username);

        if (userDBManager.getAdminCount() <= 1) {
            throw new InvalidActionException("At lest one admin must exist in the system. " +
                    "Current admin count " + userDBManager.getAdminCount());
        }

        User user = userDBManager.getUserByUsername(username);
        validateUser(user);

        User updatedUser = user
                .updateUser()
                .userUpdaterRole(UserRole.NORMAL_USER)
                .update();

        userDBManager.removeAdminRole(updatedUser);
    }

    public void removeUser(String sessionId, String username) {
        validateSession(sessionId);
        verifyAdminUserBySessionId(sessionId);
        validateUsername(username);
        User user = userDBManager.getUserByUsername(username);

        if (user.isAdmin() && userDBManager.getAdminCount() <= 1) {
            throw new InvalidActionException("At lest one admin must exist in the system. " +
                    "Current admin count " + userDBManager.getAdminCount());
        }
        
        User removedUser = userDBManager.removeUser(username);
        sessionManager.endSessionWithUserId(removedUser.getId());
    }

    private void validatePassword(String realPassword, String passwordToCheck) {
        if (!cryptography.decrypt(realPassword).equals(passwordToCheck)) {
            throw new InvalidPasswordException("Wrong password.");
        }
    }

    private void validateSession(String sessionId) {
        if (!sessionManager.isSessionActive(sessionId)) {
            throw new InactiveSessionException("Session with id:" + sessionId + " is not active. Please log in again.");
        }
    }

    private void validateUsername(String username) {
        if (!userDBManager.isUsernameUsed(username)) {
            throw new InvalidUsernameException("User with name:" + username + " does not exist.");
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidUserException("User with this data does not exist in the system.");
        }
    }

    private void verifyAdminUserBySessionId(String sessionId) {
        Session userSession = sessionManager.getSession(sessionId);
        User user = userDBManager.getUserById(userSession.getUserId());

        if (!user.isAdmin()) {
            throw new UnauthorizedAccessException("User with sessionId:" + sessionId + "is not an admin.");
        }
    }

    private User createUser(String username, String password, String firstName, String lastName, String email) {
        String encryptedPassword = cryptography.encrypt(password);
        UserRole userRole = userDBManager.getAdminCount() <= 0 ? UserRole.ADMIN : UserRole.NORMAL_USER;

        return new User(username, encryptedPassword, firstName, lastName, email, userRole);
    }

}
