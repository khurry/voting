package ru.khurry.voting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.khurry.voting.model.User;
import ru.khurry.voting.repository.UserRepository;
import ru.khurry.voting.util.UserUtils;
import ru.khurry.voting.util.ValidationUtils;
import ru.khurry.voting.web.security.AuthorizedUser;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getByEmail(email.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " is not found");
        }
        return new AuthorizedUser(user);
    }
}
