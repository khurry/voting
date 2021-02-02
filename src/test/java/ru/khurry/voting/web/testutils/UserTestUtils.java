package ru.khurry.voting.web.testutils;

import ru.khurry.voting.model.Role;
import ru.khurry.voting.model.User;
import ru.khurry.voting.web.json.JsonUtils;

import java.time.LocalDateTime;

public class UserTestUtils {
    public static final User user = new User(100012, "User", "user@yandex.ru", "password", LocalDateTime.now(), Role.USER);
    public static final User admin = new User(100013, "Admin", "admin@gmail.com", "admin", LocalDateTime.now(), Role.ADMIN);

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtils.writeAdditionProps(user, "password", passw);
    }
}
