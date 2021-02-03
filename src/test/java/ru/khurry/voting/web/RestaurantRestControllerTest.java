package ru.khurry.voting.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.khurry.voting.dto.RestaurantDto;
import ru.khurry.voting.model.Dish;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;
import ru.khurry.voting.model.User;
import ru.khurry.voting.repository.DishRepository;
import ru.khurry.voting.repository.MenuRepository;
import ru.khurry.voting.repository.RestaurantRepository;
import ru.khurry.voting.repository.UserRepository;
import ru.khurry.voting.service.RestaurantService;
import ru.khurry.voting.util.DateUtils;
import ru.khurry.voting.util.RestaurantUtils;
import ru.khurry.voting.util.UserUtils;
import ru.khurry.voting.web.json.JsonUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.khurry.voting.web.testutils.RestaurantTestUtils.*;
import static ru.khurry.voting.web.testutils.TestUtils.userHttpBasic;
import static ru.khurry.voting.web.testutils.UserTestUtils.admin;
import static ru.khurry.voting.web.testutils.UserTestUtils.user;

@SuppressWarnings("ALL")
class RestaurantRestControllerTest extends AbstractRestControllerTest {
    private static final String REST_URL = RestaurantRestController.REST_URL + "/";
    private static final int RESTAURANT_ID = restaurant1.getId();
    private static final int MENU_ID = menu1.getId();
    private static final int DISH_ID = dish1.getId();

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void getAllRestaurants() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    Assertions.assertThat(JsonUtils.readValues(result.getResponse().getContentAsString(), RestaurantDto.class))
                            .usingElementComparatorIgnoringFields("")
                            .isEqualTo(Arrays.asList(RestaurantUtils.createRestaurantDTO(menu1), RestaurantUtils.createRestaurantDTO(menu3)));
                });
    }

    @Test
    void getRestaurantWithCurrentMenu() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT_ID)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn();

        RestaurantDto actualRestaurant = JsonUtils.readValue(result.getResponse().getContentAsString(), RestaurantDto.class);
        RestaurantDto expectedRestaurant = RestaurantUtils.createRestaurantDTO(menu1);

        Assertions.assertThat(actualRestaurant).usingRecursiveComparison().ignoringFields("todayMenu.restaurant", "todayMenu.dishes.menu").isEqualTo(expectedRestaurant);
    }

    @Test
    void getRestaurantWithMenus() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT_ID + "/menus")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtils.readValue(result.getResponse().getContentAsString(), Restaurant.class))
                        .usingRecursiveComparison().ignoringCollectionOrder().ignoringFields("menus.restaurant", "menus.dishes.menu").isEqualTo(restaurant1));
    }

    @Test
    void getDish() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT_ID + "/menus/" + MENU_ID + "/dishes/" + DISH_ID)
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtils.readValue(result.getResponse().getContentAsString(), Dish.class))
                        .usingRecursiveComparison().ignoringFields("menu").isEqualTo(dish1));
    }

    @Test
    void voteFirstTime() throws Exception {
        Menu expectedMenu = new Menu(menu1);
        expectedMenu.incrementVoteCount();
        RestaurantDto expectedRestaurant = RestaurantUtils.createRestaurantDTO(expectedMenu);

        perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT_ID + "/vote")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtils.readValue(result.getResponse().getContentAsString(), RestaurantDto.class))
                        .usingRecursiveComparison().ignoringFields("todayMenu.restaurant", "todayMenu.dishes.menu").isEqualTo(expectedRestaurant));
        User votedUser = new User(user);
        votedUser.setMenu(expectedMenu);

        Assertions.assertThat(userRepository.findById(user.getId()).get()).usingRecursiveComparison()
                .ignoringFields("menu.dishes", "menu.restaurant.menus", "registered", "password").isEqualTo(votedUser);
    }

    @Test
    void changeVoteBeforeThresholdTime() throws Exception {
        Menu oldMenu = new Menu(menu1);
        menuRepository.incrementVoteCount(oldMenu.getId());

        Menu newMenu = new Menu(menu3);
        newMenu.incrementVoteCount();

        User expectedUser = new User(user);
        expectedUser.setMenu(oldMenu);

        userRepository.save(UserUtils.prepareToSave(expectedUser, passwordEncoder));
        expectedUser.setMenu(newMenu);

        RestaurantDto expectedRestaurant = RestaurantUtils.createRestaurantDTO(newMenu);

        LocalDateTime beforeThreshold = LocalDateTime.now().withHour(RestaurantService.thresholdTime.minusHours(1).getHour());
        ZoneId zoneId = ZoneId.systemDefault();

        DateUtils.setClock(Clock.fixed(beforeThreshold.atZone(zoneId).toInstant(), ZoneId.systemDefault()));

        perform(MockMvcRequestBuilders.post(REST_URL + restaurant2.getId() + "/vote")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtils.readValue(result.getResponse().getContentAsString(), RestaurantDto.class))
                        .usingRecursiveComparison().ignoringFields("todayMenu.restaurant", "todayMenu.dishes.menu").isEqualTo(expectedRestaurant));

        Assertions.assertThat(userRepository.findById(user.getId()).get()).usingRecursiveComparison()
                .ignoringFields("menu.dishes", "menu.restaurant.menus", "registered").isEqualTo(expectedUser);
        Assertions.assertThat(menuRepository.findById(oldMenu.getId()).get()).usingRecursiveComparison()
                .ignoringFields("dishes", "restaurant").isEqualTo(oldMenu);

        DateUtils.setClock(Clock.systemDefaultZone());
    }

    @Test
    void changeVoteAfterThresholdTime() throws Exception {
        Menu oldMenu = new Menu(menu1);
        oldMenu.incrementVoteCount();
        menuRepository.incrementVoteCount(oldMenu.getId());

        Menu newMenu = new Menu(menu3);
        newMenu.incrementVoteCount();

        User expectedUser = new User(user);
        expectedUser.setMenu(oldMenu);
        userRepository.save(UserUtils.prepareToSave(expectedUser, passwordEncoder));

        RestaurantDto expectedRestaurant = RestaurantUtils.createRestaurantDTO(oldMenu);

        LocalDateTime beforeThreshold = LocalDateTime.now().withHour(RestaurantService.thresholdTime.plusHours(1).getHour());
        ZoneId zoneId = ZoneId.systemDefault();
        DateUtils.setClock(Clock.fixed(beforeThreshold.atZone(zoneId).toInstant(), ZoneId.systemDefault()));

        perform(MockMvcRequestBuilders.post(REST_URL + restaurant2.getId() + "/vote")
                .with(userHttpBasic(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> Assertions.assertThat(JsonUtils.readValue(result.getResponse().getContentAsString(), RestaurantDto.class))
                        .usingRecursiveComparison().ignoringFields("todayMenu.restaurant", "todayMenu.dishes.menu").isEqualTo(expectedRestaurant));
        Assertions.assertThat(userRepository.findById(user.getId()).get()).usingRecursiveComparison()
                .ignoringFields("menu.dishes", "menu.restaurant.menus", "registered").isEqualTo(expectedUser);

        Assertions.assertThat(menuRepository.findById(menu3.getId()).get()).usingRecursiveComparison()
                .ignoringFields("dishes", "restaurant").isNotEqualTo(newMenu);

        DateUtils.setClock(Clock.systemDefaultZone());
    }

    @Test
    void createRestaurant() throws Exception {
        Restaurant newRestaurant = new Restaurant(null, "new rest");
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtils.writeValue(newRestaurant)))
                .andExpect(status().isCreated());

        Restaurant created = JsonUtils.readValue(action.andReturn().getResponse().getContentAsString(), Restaurant.class);
        newRestaurant.setId(created.getId());
        Assertions.assertThat(created).usingRecursiveComparison().isEqualTo(newRestaurant);
    }

    @Test
    void createMenu() throws Exception {
        Menu newMenu = new Menu(null, LocalDate.now().minusDays(1), restaurant1, 0);

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT_ID + "/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtils.writeValue(newMenu)))
                .andExpect(status().isCreated());

        Menu created = JsonUtils.readValue(action.andReturn().getResponse().getContentAsString(), Menu.class);
        newMenu.setId(created.getId());
        Assertions.assertThat(created).usingRecursiveComparison().ignoringFields("dishes", "restaurant").isEqualTo(newMenu);
    }

    @Test
    void createDish() throws Exception {
        Dish newDish = new Dish(null, "new Dish", 4000, null);

        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT_ID + "/menus/" + MENU_ID + "/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtils.writeValue(newDish)))
                .andExpect(status().isCreated());

        Dish created = JsonUtils.readValue(action.andReturn().getResponse().getContentAsString(), Dish.class);
        newDish.setId(created.getId());
        Assertions.assertThat(created).usingRecursiveComparison().ignoringFields("menu").isEqualTo(newDish);
    }

    @Test
    void updateRestaurant() throws Exception {
        Restaurant updatedRestaurant = new Restaurant(restaurant1);
        updatedRestaurant.setName("UpdatedRestaurant");

        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtils.writeValue(updatedRestaurant)))
                .andExpect(status().isNoContent());

        Restaurant actualRestaurant = restaurantRepository.findById(updatedRestaurant.getId()).get();
        Assertions.assertThat(actualRestaurant).usingRecursiveComparison().ignoringFields("menus.restaurant").isEqualTo(updatedRestaurant);
    }

    @Test
    void updateMenu() throws Exception {
        Menu updatedMenu = new Menu(menu1);
        updatedMenu.setCreated(LocalDate.of(1998, 1, 1));

        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT_ID + "/menus/" + MENU_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtils.writeValue(updatedMenu)))
                .andExpect(status().isNoContent());

        Menu actualMenu = menuRepository.findById(updatedMenu.getId()).get();
        Assertions.assertThat(actualMenu).usingRecursiveComparison().ignoringFields("restaurant", "dishes.menu").isEqualTo(updatedMenu);
    }

    @Test
    void updateDish() throws Exception {
        Dish updatedDish = new Dish(dish1);
        updatedDish.setName("UpdatedDish");

        perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT_ID + "/menus/" + MENU_ID + "/dishes/" + DISH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(JsonUtils.writeValue(updatedDish)))
                .andExpect(status().isNoContent());

        Dish actualDish = dishRepository.findById(updatedDish.getId()).get();
        Assertions.assertThat(actualDish).usingRecursiveComparison().ignoringFields("menu").isEqualTo(updatedDish);
    }

    @Test
    void deleteRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT_ID)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(restaurantRepository.findById(RESTAURANT_ID).isPresent());
    }

    @Test
    void deleteMenu() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT_ID + "/menus/" + MENU_ID)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(menuRepository.findById(MENU_ID).isPresent());
    }

    @Test
    void deleteDish() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT_ID + "/menus/" + MENU_ID + "/dishes/" + DISH_ID)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(dishRepository.findById(DISH_ID).isPresent());
    }
}