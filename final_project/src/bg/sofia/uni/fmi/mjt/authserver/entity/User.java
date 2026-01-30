package bg.sofia.uni.fmi.mjt.authserver.entity;

import java.util.Objects;
import java.util.UUID;

public class User {

    private final String id;
    private final String userName;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final UserRole userRole;

    public User(String userName, String password, String firstName, String lastName, String email, UserRole userRole) {
        this.id = generateId();
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userRole = userRole;
    }

    private User(String id, String userName, String password,
                 String firstName, String lastName, String email, UserRole userRole) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userRole = userRole;
    }

    public UserUpdater updateUser() {
        return new UserUpdater(this);
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return userRole;
    }

    public boolean isAdmin() {
        return userRole.equals(UserRole.ADMIN);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static class UserUpdater {

        private final String id;
        private String userName;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private UserRole userRole;

        private UserUpdater(User currentUser) {
            this.id = currentUser.id;
            this.userName = currentUser.userName;
            this.password = currentUser.password;
            this.firstName = currentUser.firstName;
            this.lastName = currentUser.lastName;
            this.email = currentUser.email;
            this.userRole = currentUser.userRole;
        }

        public UserUpdater updateUsername(String userName) {
            if (userName != null) {
                this.userName = userName;
            }
            return this;
        }

        public UserUpdater updatePassword(String password) {
            if (password != null) {
                this.password = password;
            }
            return this;
        }

        public UserUpdater updateFirstName(String firstName) {
            if (firstName != null) {
                this.firstName = firstName;
            }
            return this;
        }

        public UserUpdater updateLastName(String lastName) {
            if (lastName != null) {
                this.lastName = lastName;
            }
            return this;
        }

        public UserUpdater updateEmail(String email) {
            if (email != null) {
                this.email = email;
            }
            return this;
        }

        public UserUpdater userUpdaterRole(UserRole userRole) {
            if (userRole != null) {
                this.userRole = userRole;
            }
            return this;
        }

        public User update() {
            return new User(id, userName, password, firstName, lastName, email, userRole);
        }

    }

}
