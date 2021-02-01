package ru.khurry.voting.web;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.khurry.voting.model.User;
import ru.khurry.voting.repository.UserRepository;
import ru.khurry.voting.util.UserUtils;
import ru.khurry.voting.util.ValidationUtils;

import java.util.List;

public class AbstractUserRestController {
    protected final UserRepository userRepository;

    protected final PasswordEncoder passwordEncoder;

    public AbstractUserRestController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUser(int id) {
        return ValidationUtils.checkNotFound(userRepository.findById(id));
    }

    public User create(User user) {
        return prepareAndSave(user);
    }

    public void update(int id, User user) {
        ValidationUtils.checkConsistentId(id, user);
        prepareAndSave(user);
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }

    private User prepareAndSave(User user) {
        return userRepository.save(UserUtils.prepareToSave(user, passwordEncoder));
    }

}
