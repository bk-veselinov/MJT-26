package bg.sofia.uni.fmi.mjt.authserver.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class UserTest {

    @Test
    void testUpdaterUpdateUsernameChangesUsername() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().updateUsername("newfName").update();
        assertEquals("newfName", updated.getUserName(),
                "updateUsername should change the username in the updated user");
    }

    @Test
    void testUpdaterUpdateUsernameIgnoresNull() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().updateUsername(null).update();
        assertEquals("username", updated.getUserName(),
                "updateUsername should keep the original username when null is passed");
    }

    @Test
    void testUpdaterUpdatePasswordChangesPassword() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().updatePassword("newPass").update();
        assertEquals("newPass", updated.getPassword(),
                "updatePassword should change the password in the updated user");
    }

    @Test
    void testUpdaterUpdatePasswordIgnoresNull() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().updatePassword(null).update();
        assertEquals("pass", updated.getPassword(),
                "updatePassword should keep the original password when null is passed");
    }

    @Test
    void testUpdaterUserUpdaterRoleChangesRole() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().userUpdaterRole(UserRole.ADMIN).update();
        assertEquals(UserRole.ADMIN, updated.getRole(),
                "userUpdaterRole should change the role in the updated user");
    }

    @Test
    void testUpdaterUserUpdaterRoleIgnoresNull() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().userUpdaterRole(null).update();
        assertEquals(UserRole.NORMAL_USER, updated.getRole(),
                "userUpdaterRole should keep the original role when null is passed");
    }

    @Test
    void testUpdaterPreservesId() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().updateUsername("newfName").update();
        assertEquals(user.getId(), updated.getId(),
                "update should preserve the original user's id");
    }

    @Test
    void testUpdaterChainingMultipleUpdates() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser()
                .updateUsername("newfName")
                .updatePassword("newPass")
                .updateLastName("newLName")
                .updateEmail("new@test.com")
                .userUpdaterRole(UserRole.ADMIN)
                .update();

        assertEquals("newfName", updated.getUserName(),
                "chained update should set new username");
        assertEquals("newPass", updated.getPassword(),
                "chained update should set new password");
        assertEquals("newLName", updated.getLastName(),
                "chained update should set new last name");
        assertEquals("new@test.com", updated.getEmail(),
                "chained update should set new email");
        assertEquals(UserRole.ADMIN, updated.getRole(),
                "chained update should set new role");
        assertEquals(user.getId(), updated.getId(),
                "chained update should preserve the original id");
    }

    @Test
    void testUpdaterNoChangesPreservesAllFields() {
        User user = new User("username", "pass", "fName", "lName", "username@test.com", UserRole.NORMAL_USER);
        User updated = user.updateUser().update();

        assertEquals(user.getId(), updated.getId(),
                "update with no changes should preserve id");
        assertEquals(user.getUserName(), updated.getUserName(),
                "update with no changes should preserve username");
        assertEquals(user.getPassword(), updated.getPassword(),
                "update with no changes should preserve password");
        assertEquals(user.getLastName(), updated.getLastName(),
                "update with no changes should preserve last name");
        assertEquals(user.getEmail(), updated.getEmail(),
                "update with no changes should preserve email");
        assertEquals(user.getRole(), updated.getRole(),
                "update with no changes should preserve role");
    }
}
