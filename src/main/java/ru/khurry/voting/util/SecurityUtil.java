package ru.khurry.voting.util;

import ru.khurry.voting.model.Role;
import ru.khurry.voting.model.User;

import java.time.LocalDateTime;

public class SecurityUtil {
    private static User user = new User(100012, "UserName", "user@gmail.com", "password", LocalDateTime.now(), Role.USER);
    public static User getAuthUser() {
        return user;
    }

    public static int getAuthUserId() { return user.getId(); }
}
