package ru.khurry.voting.web.security;

import ru.khurry.voting.model.User;

import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = 1L;

    private final User user;

    public AuthorizedUser(User user) {
        super(user.getEmail(), user.getPassword(), true, true, true, true, user.getRoles());
        this.user = Objects.requireNonNull(user);
    }

    public int getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }
}