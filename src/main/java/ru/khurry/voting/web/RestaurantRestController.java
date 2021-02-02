package ru.khurry.voting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.khurry.voting.dto.RestaurantDto;
import ru.khurry.voting.model.Dish;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;
import ru.khurry.voting.service.RestaurantService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(RestaurantRestController.REST_URL)
public class RestaurantRestController {
    public static final String REST_URL = "/restaurants";

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantRestController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<RestaurantDto> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public RestaurantDto getRestaurantWithCurrentMenu(@PathVariable int id) {
        return restaurantService.getRestaurantWithCurrentMenu(id);
    }

    @GetMapping("/{restaurantId}/menus")
    public Restaurant getRestaurantWithMenus(@PathVariable int restaurantId) {
        return restaurantService.getRestaurantWithMenus(restaurantId);
    }

    @GetMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    public Dish getDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId) {
        return restaurantService.getDish(dishId);
    }

    @PostMapping("/{id}/vote")
    public RestaurantDto vote(@PathVariable int id) {
        return restaurantService.vote(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody @Valid Restaurant restaurant) {
        Restaurant newRestaurant = restaurantService.createRestaurant(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(newRestaurant.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newRestaurant);
    }

    @PostMapping(value = "/{restaurantId}/menus", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> createMenu(@PathVariable int restaurantId, @RequestBody @Valid Menu menu) {
        Menu newMenu = restaurantService.createMenu(restaurantId, menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menus/{menuId}")
                .buildAndExpand(restaurantId, newMenu.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newMenu);
    }

    @PostMapping(value = "/{restaurantId}/menus/{menuId}/dishes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createDish(@PathVariable int restaurantId, @PathVariable int menuId, @RequestBody @Valid Dish dish) {
        Dish newDish = restaurantService.createDish(menuId, dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{restaurantId}/menus/{menuId}/dishes/{dishId}")
                .buildAndExpand(restaurantId, menuId, newDish.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newDish);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRestaurant(@PathVariable int id, @RequestBody @Valid Restaurant restaurant) {
        restaurantService.updateRestaurant(id, restaurant);
    }

    @PutMapping(value = "/{restaurantId}/menus/{menuId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMenu(@PathVariable int restaurantId, @PathVariable int menuId, @RequestBody @Valid Menu menu) {
        restaurantService.updateMenu(restaurantId, menuId, menu);
    }

    @PutMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId, @RequestBody Dish dish) {
        restaurantService.updateDish(menuId, dishId, dish);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable int id) {
        restaurantService.deleteRestaurant(id);
    }

    @DeleteMapping("/{restaurantId}/menus/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(@PathVariable int restaurantId, @PathVariable int menuId) {
        restaurantService.deleteMenu(menuId);
    }

    @DeleteMapping("/{restaurantId}/menus/{menuId}/dishes/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDish(@PathVariable int restaurantId, @PathVariable int menuId, @PathVariable int dishId) {
        restaurantService.deleteDish(dishId);
    }
}
