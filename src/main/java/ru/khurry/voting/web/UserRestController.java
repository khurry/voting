package ru.khurry.voting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.khurry.voting.model.User;
import ru.khurry.voting.repository.UserRepository;
import ru.khurry.voting.service.UserService;
import ru.khurry.voting.web.security.AuthorizedUser;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(UserRestController.REST_URL)
public class UserRestController extends AbstractUserRestController {
    public static final String REST_URL = "/users";

    @Autowired
    public UserRestController(UserService userService) {
        super(userService);
    }

    @GetMapping
    public List<User> getAll() {
        return super.getAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return super.getUser(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createWithResponse(@RequestBody @Valid User user) {
        User newUser = super.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newUser);
    }

    @PutMapping(value = "/{id}",  consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @RequestBody @Valid User user) {
        super.update(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

}
