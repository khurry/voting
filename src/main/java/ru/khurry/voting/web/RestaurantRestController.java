package ru.khurry.voting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.khurry.voting.dto.RestaurantDTO;
import ru.khurry.voting.model.Dish;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;
import ru.khurry.voting.model.User;
import ru.khurry.voting.repository.DishRepository;
import ru.khurry.voting.repository.MenuRepository;
import ru.khurry.voting.repository.RestaurantRepository;
import ru.khurry.voting.repository.UserRepository;
import ru.khurry.voting.util.DateUtil;
import ru.khurry.voting.util.RestaurantUtil;
import ru.khurry.voting.util.SecurityUtil;
import ru.khurry.voting.util.ValidationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantRestController {
    public static final LocalTime thresholdTime = LocalTime.of(11, 0);
    public static final String REST_URL = "/restaurants";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @GetMapping
    public List<RestaurantDTO> getAllRestaurants() {
        return RestaurantUtil.createRestaurantDTOList(menuRepository.findAllByToday());
    }

    @GetMapping("/{id}")
    public RestaurantDTO getRestaurantWithCurrentMenu(@PathVariable int id) {
        return RestaurantUtil.createRestaurantDTO(ValidationUtil.checkNotFound(menuRepository.findByTodayAndRestaurantId(id)));
    }

    @GetMapping("/{restaurantId}/menus")
    public Restaurant getRestaurantWithMenus(@PathVariable int restaurantId) {
        return ValidationUtil.checkNotFound(restaurantRepository.findByIdWithMenus(restaurantId));
    }

    @GetMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    public Dish getDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId) {
        return ValidationUtil.checkNotFound(dishRepository.findByIdAndMenuId(menuId, dishId));
    }

    @PostMapping("/{id}/vote")
    @Transactional
    public RestaurantDTO vote(@PathVariable int id) {
        Menu todayMenu = ValidationUtil.checkNotFound(menuRepository.findByTodayAndRestaurantId(id));
        User user = ValidationUtil.checkNotFound(userRepository.findById(SecurityUtil.getAuthUserId()));


        Menu oldMenu = user.getMenu();
        if (oldMenu != null) {
            if (DateUtil.isAfter(thresholdTime) || oldMenu.equals(todayMenu))
                return RestaurantUtil.createRestaurantDTO(oldMenu);
            else {
                oldMenu.decrementVoteCount();
            }
        }
        todayMenu.incrementVoteCount();
        user.setMenu(todayMenu);

        return RestaurantUtil.createRestaurantDTO(todayMenu);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody @Valid Restaurant restaurant) {
        Restaurant newRestaurant = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(newRestaurant.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newRestaurant);
    }

    @PostMapping(value = "/{restaurantId}/menus", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> createMenu(@PathVariable int restaurantId, @RequestBody @Valid Menu menu) {
        Restaurant restaurant = ValidationUtil.checkNotFound(restaurantRepository.findByIdWithMenus(restaurantId));
        menu.setRestaurant(restaurant);
        Menu newMenu = menuRepository.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menus/{menuId}")
                .buildAndExpand(restaurantId, newMenu.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newMenu);
    }

    @PostMapping(value = "/{restaurantId}/menus/{menuId}/dishes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createDish(@PathVariable int restaurantId, @PathVariable int menuId, @RequestBody @Valid Dish dish) {
        Menu menu = ValidationUtil.checkNotFound(menuRepository.findById(menuId));
        dish.setMenu(menu);

        Dish newDish = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menus/{menuId}/dishes/{dishId}")
                .buildAndExpand(restaurantId, menuId, newDish.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newDish);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRestaurant(@PathVariable int id, @RequestBody @Valid Restaurant restaurant) {
        ValidationUtil.checkConsistentId(id, restaurant);
        restaurantRepository.save(restaurant);
    }

    @PutMapping(value = "/{restaurantId}/menus/{menuId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMenu(@PathVariable int restaurantId, @PathVariable int menuId, @RequestBody @Valid Menu menu) {
        ValidationUtil.checkConsistentId(menuId, menu);
        menu.setVoteCount(0);
        menuRepository.save(menu);
    }

    @PutMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId, @RequestBody Dish dish) {
        Menu menu = ValidationUtil.checkNotFound(menuRepository.findById(menuId));
        menu.setVoteCount(0);
        menuRepository.save(menu);
        dish.setMenu(menu);
        dishRepository.save(dish);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable int id) {
        restaurantRepository.deleteById(id);
    }

    @DeleteMapping("/{restaurantId}/menus/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(@PathVariable int restaurantId, @PathVariable int menuId) {
        menuRepository.deleteById(menuId);
    }

    @DeleteMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId) {
        dishRepository.deleteById(dishId);
    }
}
