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
import ru.khurry.voting.util.NotFoundException;
import ru.khurry.voting.web.json.JsonUtil;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khurry.voting.web.testutils.UserTestUtils.admin;
import static ru.khurry.voting.web.testutils.UserTestUtils.user;

@SuppressWarnings("ConstantConditions")
public class UserRestControllerTest extends AbstractRestControllerTest {
    @Autowired
    private UserRepository repository;

    private static final String REST_URL = "/users/";
    private static final int USER_ID = user.getId();

    @Test
    public void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
//                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtil.readValues(result.getResponse().getContentAsString(), User.class))
                        .usingElementComparatorIgnoringFields("registered", "menu.restaurant", "menu.dishes").isEqualTo(Arrays.asList(user, admin)));
    }

    @Test
    public void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID))
//                .with(userHttpBasic(admin)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtil.readValue(result.getResponse().getContentAsString(), User.class))
                        .usingRecursiveComparison().ignoringFields("registered", "menu.restaurant", "menu.dishes").isEqualTo(user));
    }

    @Test
    public void create() throws Exception {
        User newUser = new User(null, "newUser", "newemail@gmail.com", "pass", LocalDateTime.now(), Role.USER);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
//            .with(userHttpBasic(admin))
//            .content(jsonWithPassword(newUser, "newPass")))
                .content(JsonUtil.writeValue(newUser)))
                .andExpect(status().isCreated());

        User created = JsonUtil.readValue(action.andReturn().getResponse().getContentAsString(), User.class);
        newUser.setId(created.getId());
        Assertions.assertThat(created).usingRecursiveComparison().ignoringFields("registered").isEqualTo(newUser);
    }

    @Test
    public void update() throws Exception {
        User updatedUser = new User(user);
        updatedUser.setName("UpdatedUser");

        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
//            .with(userHttpBasic(admin))
//            .content(jsonWithPassword(newUser, "newPass")))
                .content(JsonUtil.writeValue(updatedUser)))
                .andExpect(status().isNoContent());

        User actualUser = repository.findById(updatedUser.getId()).orElseThrow(NotFoundException::new);
        Assertions.assertThat(actualUser).usingRecursiveComparison().ignoringFields("menu.restaurant", "menu.dishes").isEqualTo(updatedUser);
    }

    @Test
    public void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID))
//                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(repository.findById(USER_ID).isPresent());
    }
}