package ru.khurry.voting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.khurry.voting.dto.RestaurantDto;
import ru.khurry.voting.model.Dish;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;
import ru.khurry.voting.model.User;
import ru.khurry.voting.repository.DishRepository;
import ru.khurry.voting.repository.MenuRepository;
import ru.khurry.voting.repository.RestaurantRepository;
import ru.khurry.voting.repository.UserRepository;
import ru.khurry.voting.util.DateUtils;
import ru.khurry.voting.util.RestaurantUtils;
import ru.khurry.voting.util.SecurityUtils;
import ru.khurry.voting.util.ValidationUtils;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(RestaurantRestController.REST_URL)
public class RestaurantRestController {
    public static final LocalTime thresholdTime = LocalTime.of(11, 0);
    public static final String REST_URL = "/restaurants";

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Autowired
    public RestaurantRestController(RestaurantRepository restaurantRepository, DishRepository dishRepository, UserRepository userRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
    }

    @GetMapping
    public List<RestaurantDto> getAllRestaurants() {
        return RestaurantUtils.createRestaurantDTOList(menuRepository.findAllByToday());
    }

    @GetMapping("/{id}")
    public RestaurantDto getRestaurantWithCurrentMenu(@PathVariable int id) {
        return RestaurantUtils.createRestaurantDTO(ValidationUtils.checkNotFound(menuRepository.findByTodayAndRestaurantId(id)));
    }

    @GetMapping("/{restaurantId}/menus")
    public Restaurant getRestaurantWithMenus(@PathVariable int restaurantId) {
        return ValidationUtils.checkNotFound(restaurantRepository.findByIdWithMenus(restaurantId));
    }

    @GetMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    public Dish getDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId) {
        return ValidationUtils.checkNotFound(dishRepository.findByIdAndMenuId(menuId, dishId));
    }

    @PostMapping("/{id}/vote")
    @Transactional
    public RestaurantDto vote(@PathVariable int id) {
        Menu todayMenu = ValidationUtils.checkNotFound(menuRepository.findByTodayAndRestaurantId(id));
        User user = ValidationUtils.checkNotFound(userRepository.findById(SecurityUtils.authUserId()));


        Menu oldMenu = user.getMenu();
        if (oldMenu != null) {
            if (DateUtils.isAfter(thresholdTime) || oldMenu.equals(todayMenu))
                return RestaurantUtils.createRestaurantDTO(oldMenu);
            else {
                oldMenu.decrementVoteCount();
            }
        }
        todayMenu.incrementVoteCount();
        user.setMenu(todayMenu);

        return RestaurantUtils.createRestaurantDTO(todayMenu);
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
        Restaurant restaurant = ValidationUtils.checkNotFound(restaurantRepository.findByIdWithMenus(restaurantId));
        menu.setRestaurant(restaurant);
        Menu newMenu = menuRepository.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menus/{menuId}")
                .buildAndExpand(restaurantId, newMenu.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newMenu);
    }

    @PostMapping(value = "/{restaurantId}/menus/{menuId}/dishes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createDish(@PathVariable int restaurantId, @PathVariable int menuId, @RequestBody @Valid Dish dish) {
        Menu menu = ValidationUtils.checkNotFound(menuRepository.findById(menuId));
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
        ValidationUtils.checkConsistentId(id, restaurant);
        restaurantRepository.save(restaurant);
    }

    @PutMapping(value = "/{restaurantId}/menus/{menuId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMenu(@PathVariable int restaurantId, @PathVariable int menuId, @RequestBody @Valid Menu menu) {
        ValidationUtils.checkConsistentId(menuId, menu);
        menu.setVoteCount(0);
        menuRepository.save(menu);
    }

    @PutMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId, @RequestBody Dish dish) {
        Menu menu = ValidationUtils.checkNotFound(menuRepository.findById(menuId));
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
