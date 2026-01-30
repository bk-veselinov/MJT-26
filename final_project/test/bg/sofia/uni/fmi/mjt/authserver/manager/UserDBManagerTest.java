package bg.sofia.uni.fmi.mjt.authserver.manager;

import bg.sofia.uni.fmi.mjt.authserver.entity.User;
import bg.sofia.uni.fmi.mjt.authserver.entity.UserRole;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUserException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.authserver.util.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDBManagerTest {

    @Mock
    private FileHandler fileHandler;

    private UserDBManager userDBManager;

    @BeforeEach
    void setUp() {
        when(fileHandler.loadData()).thenReturn("[]");
        userDBManager = new UserDBManager(fileHandler);
    }

    @Test
    void testAddUserAddsUserSuccessfully() {
        User user = new User("testUser", "pass", "fName", "lName", "test@test.com", UserRole.NORMAL_USER);
        userDBManager.addUser(user);
        assertTrue(userDBManager.isUsernameUsed("testUser"),
                "isUsernameUsed should return true after adding a user");
    }

    @Test
    void testAddUserThrowsForDuplicateUsername() {
        User user1 = new User("testUser", "pass", "fName", "lName", "test@test.com", UserRole.NORMAL_USER);
        userDBManager.addUser(user1);

        User user2 = new User("testUser", "pass2", "fName1", "lName", "fName1@test.com", UserRole.NORMAL_USER);
        assertThrows(InvalidActionException.class, () -> userDBManager.addUser(user2),
                "addUser should throw InvalidActionException for duplicate username");
    }

    @Test
    void testGetUserByIdReturnsCorrectUser() {
        User user = new User("testUser", "pass", "fName", "lName", "test@test.com", UserRole.NORMAL_USER);
        userDBManager.addUser(user);
        User result = userDBManager.getUserById(user.getId());
        assertEquals(user, result, "getUserById should return the correct user");
    }

    @Test
    void testGetUserByIdThrowsForNonExistentUser() {
        assertThrows(InvalidUserException.class, () -> userDBManager.getUserById("nonExistentId"),
                "getUserById should throw InvalidUserException for non-existent user id");
    }

    @Test
    void testGetUserByUsernameReturnsCorrectUser() {
        User user = new User("testUser", "pass", "fName", "lName", "test@test.com", UserRole.NORMAL_USER);
        userDBManager.addUser(user);
        User result = userDBManager.getUserByUsername("testUser");
        assertEquals(user, result, "getUserByUsername should return the correct user");
    }

    @Test
    void testGetUserByUsernameThrowsForNonExistentUsername() {
        assertThrows(InvalidUsernameException.class, () -> userDBManager.getUserByUsername("unknown"),
                "getUserByUsername should throw InvalidUsernameException for non-existent username");
    }

    @Test
    void testUpdateUserDataUpdatesSuccessfully() {
        User user = new User("testUser", "pass", "fName", "lName", "test@test.com", UserRole.NORMAL_USER);
        userDBManager.addUser(user);

        User updatedUser = user.updateUser().updateFirstName("fName1").update();
        userDBManager.updateUserData(user.getId(), updatedUser);

        User result = userDBManager.getUserById(user.getId());
        assertNotNull(result, "getUserById should return a non-null user after update");
    }

    @Test
    void testUpdateUserDataThrowsForNonExistentUser() {

        User user = new User("test", "pass", "fName", "lName", "test@test.com", UserRole.NORMAL_USER);
        assertThrows(InvalidUserException.class, () -> userDBManager.updateUserData("nonExistent", user),
                "updateUserData should throw InvalidUserException for non-existent user id");
    }

    @Test
    void testRemoveUserRemovesSuccessfully() {
        User user = new User("testUser", "pass", "fName", "lName", "test@test.com", UserRole.NORMAL_USER);
        userDBManager.addUser(user);

        User removed = userDBManager.removeUser("testUser");
        assertNotNull(removed, "removeUser should return the removed user");
        assertFalse(userDBManager.isUsernameUsed("testUser"),
                "isUsernameUsed should return false after removing the user");
    }

    @Test
    void testRemoveUserThrowsForNonExistentUsername() {
        assertThrows(InvalidUsernameException.class, () -> userDBManager.removeUser("unknown"),
                "removeUser should throw InvalidUsernameException for non-existent username");
    }

    @Test
    void testIsUsernameUsedReturnsFalseForUnknownUsername() {
        assertFalse(userDBManager.isUsernameUsed("unknown"),
                "isUsernameUsed should return false for an unknown username");
    }

    @Test
    void testGetAdminCountReturnsZeroInitially() {
        assertEquals(0, userDBManager.getAdminCount(),
                "getAdminCount should return 0 when no users exist");
    }

    @Test
    void testAddAdminRoleIncrementsAdminCount() {
        User user = new User("admin", "pass", "Admin", "User", "admin@test.com", UserRole.NORMAL_USER);
        userDBManager.addUser(user);

        User updatedUser = user.updateUser().userUpdaterRole(UserRole.ADMIN).update();
        userDBManager.addAdminRole(updatedUser);

        assertEquals(1, userDBManager.getAdminCount(),
                "getAdminCount should return 1 after adding admin role");
    }

    @Test
    void testRemoveAdminRoleDecrementsAdminCount() {
        User user = new User("admin", "pass", "Admin", "User", "admin@test.com", UserRole.ADMIN);
        userDBManager.addUser(user);

        User updatedUser = user.updateUser().userUpdaterRole(UserRole.NORMAL_USER).update();
        userDBManager.removeAdminRole(updatedUser);

        assertEquals(0, userDBManager.getAdminCount(),
                "getAdminCount should return 0 after removing admin role");
    }
}