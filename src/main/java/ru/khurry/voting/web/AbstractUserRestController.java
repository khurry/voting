package ru.khurry.voting.web;

import ru.khurry.voting.model.User;
import ru.khurry.voting.service.UserService;

import java.util.List;

public class AbstractUserRestController {

    private final UserService userService;

    public AbstractUserRestController(UserService userService) {
        this.userService = userService;
    }

    public List<User> getAll() {
        return userService.getAll();
    }

    public User getUser(int id) {
        return userService.getUser(id);
    }

    public User create(User user) {
        return userService.create(user);
    }

    public void update(int id, User user) {
        userService.update(id, user);
    }

    public void delete(int id) {
        userService.delete(id);
    }
}
