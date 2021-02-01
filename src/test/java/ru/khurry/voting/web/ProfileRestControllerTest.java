package ru.khurry.voting.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khurry.voting.model.Role;
import ru.khurry.voting.model.User;
import ru.khurry.voting.repository.UserRepository;
import ru.khurry.voting.util.exception.NotFoundException;
import ru.khurry.voting.web.json.JsonUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khurry.voting.web.testutils.TestUtils.userHttpBasic;
import static ru.khurry.voting.web.testutils.UserTestUtils.user;

@SuppressWarnings("ConstantConditions")
class ProfileRestControllerTest extends AbstractRestControllerTest {
    @Autowired
    private UserRepository repository;

    private static final String REST_URL = "/profile/";
    private static final int USER_ID = user.getId();

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtils.readValue(result.getResponse().getContentAsString(), User.class))
                        .usingRecursiveComparison().ignoringFields("registered", "menu.restaurant", "menu.dishes", "password").isEqualTo(user));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(user)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(repository.findById(USER_ID).isPresent());
    }

    @Test
    void register() throws Exception {
        User newUser = new User(null, "newUser", "newemail@gmail.com", "pass", LocalDateTime.now(), Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.writeValue(newUser)))
                .andExpect(status().isCreated());

        User created = JsonUtils.readValue(action.andReturn().getResponse().getContentAsString(), User.class);
        newUser.setId(created.getId());
        Assertions.assertThat(created).usingRecursiveComparison().ignoringFields("registered", "password").isEqualTo(newUser);
    }

    @Test
    void update() throws Exception {
        User updatedUser = new User(user);
        updatedUser.setName("UpdatedUser");

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(user))
                .content(JsonUtils.writeValue(updatedUser)))
                .andExpect(status().isNoContent());

        User actualUser = repository.findById(updatedUser.getId()).orElseThrow(NotFoundException::new);
        Assertions.assertThat(actualUser).usingRecursiveComparison().ignoringFields("menu.restaurant", "menu.dishes", "password").isEqualTo(updatedUser);
    }
}