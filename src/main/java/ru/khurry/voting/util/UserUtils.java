package ru.khurry.voting.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import ru.khurry.voting.model.User;

import java.time.LocalDateTime;

public class UserUtils {

    public static User prepareToSave(User user, PasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? passwordEncoder.encode(password) : password);
        user.setEmail(user.getEmail().toLowerCase());
        if (user.getRegistered() == null) user.setRegistered(LocalDateTime.now());
        return user;
    }
}
