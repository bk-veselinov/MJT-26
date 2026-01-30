package bg.sofia.uni.fmi.mjt.authserver.manager;

import bg.sofia.uni.fmi.mjt.authserver.util.FileHandler;
import bg.sofia.uni.fmi.mjt.authserver.entity.UserRole;
import bg.sofia.uni.fmi.mjt.authserver.entity.User;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUserException;
import bg.sofia.uni.fmi.mjt.authserver.exception.InvalidUsernameException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

public class UserDBManager {

    private final Gson gson;
    private final FileHandler fileHandler;
    private final ConcurrentHashMap<String, User> userIdToUser;
    private final ConcurrentHashMap<String, User> usernameToUser;

    private int adminCount = 0;

    public UserDBManager(String dbFileName) {
        gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        fileHandler = new FileHandler(dbFileName);
        this.userIdToUser = jsonToUsers(fileHandler.loadData());
        usernameToUser = new ConcurrentHashMap<>();
        populateHelperFields();
    }

    UserDBManager(FileHandler fileHandler) {
        this.gson = new Gson();
        this.fileHandler = fileHandler;
        this.userIdToUser = jsonToUsers(fileHandler.loadData());
        this.usernameToUser = new ConcurrentHashMap<>();
        populateHelperFields();
    }

    private void populateHelperFields() {
        for (User user : userIdToUser.values()) {
            usernameToUser.put(user.getUserName(), user);

            if (user.getRole() == UserRole.ADMIN) {
                adminCount++;
            }
        }
    }

    public User getUserById(String userId) {
        validateUserId(userId);
        return userIdToUser.get(userId);
    }

    public User getUserByUsername(String username) {
        validateUsername(username);
        return usernameToUser.get(username);
    }

    public void updateUserData(String userId, User newUser) {
        validateUserId(userId);

        User oldUser = userIdToUser.get(userId);
        removeUserFromDB(oldUser);
        addUserToDB(newUser);

        updateFileData();
    }

    public int getAdminCount() {
        return adminCount;
    }

    public void removeAdminRole(User user) {
        adminCount--;

        updateUserData(user.getId(), user);
    }

    public void addAdminRole(User user) {
        adminCount++;

        updateUserData(user.getId(), user);
    }

    public void addUser(User user) {
        if (isUsernameUsed(user.getUserName())) {
            throw new InvalidActionException("This username is already used.");
        }
        addUserToDB(user);

        if (user.getRole().equals(UserRole.ADMIN)) {
            adminCount++;
        }

        updateFileData();
    }

    public User removeUser(String username) {
        validateUsername(username);

        User user = usernameToUser.get(username);
        removeUserFromDB(user);
        updateFileData();

        return user;
    }

    public boolean isUsernameUsed(String username) {
        return usernameToUser.containsKey(username);
    }

    private void removeUserFromDB(User user) {
        userIdToUser.remove(user.getId());
        usernameToUser.remove(user.getUserName());
    }

    private void addUserToDB(User user) {
        userIdToUser.put(user.getId(), user);
        usernameToUser.put(user.getUserName(), user);
    }

    private void validateUsername(String username) {
        if (!isUsernameUsed(username)) {
            throw new InvalidUsernameException("User with this username does not exist");
        }
    }

    private void validateUserId(String userId) {
        if (!userIdToUser.containsKey(userId)) {
            throw new InvalidUserException("The user does not exist");
        }
    }

    private void updateFileData() {
        fileHandler.writeData(usersToJson(), false);
    }

    private String usersToJson() {
        Type type = new TypeToken<ConcurrentHashMap<String, User>>() {
        }.getType();

        return gson.toJson(userIdToUser, type);
    }

    private ConcurrentHashMap<String, User> jsonToUsers(String json) {
        Type type = new TypeToken<ConcurrentHashMap<String, User>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

}
