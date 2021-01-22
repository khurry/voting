package ru.khurry.voting.web.testdata;

import ru.khurry.voting.model.Role;
import ru.khurry.voting.model.User;
import ru.khurry.voting.web.json.JsonUtil;

import java.time.LocalDateTime;

public class UserTestData {
    public static User user = new User(100012, "User", "user@yandex.ru", "password", LocalDateTime.now(), Role.USER);
    public static User admin = new User(100013, "Admin", "admin@gmail.com", "admin", LocalDateTime.now(), Role.ADMIN);


    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }


}
