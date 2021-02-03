package ru.khurry.voting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalTime;
import java.util.List;

import static ru.khurry.voting.util.ValidationUtils.checkConsistentId;
import static ru.khurry.voting.util.ValidationUtils.checkNotFound;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    public static final LocalTime thresholdTime = LocalTime.of(11, 0);

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, MenuRepository menuRepository, DishRepository dishRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RestaurantDto vote(int restaurantId) {
        Menu todayMenu = checkNotFound(menuRepository.findByTodayAndRestaurantId(restaurantId));
        User user = checkNotFound(userRepository.findById(SecurityUtils.authUserId()));

        Menu oldMenu = user.getMenu();
        if (oldMenu != null) {
            if (DateUtils.isAfter(thresholdTime, oldMenu.getCreated()) || oldMenu.equals(todayMenu))
                return RestaurantUtils.createRestaurantDTO(oldMenu);
            else {
                oldMenu.decrementVoteCount();
            }
        }
        todayMenu.incrementVoteCount();
        user.setMenu(todayMenu);

        return RestaurantUtils.createRestaurantDTO(todayMenu);
    }

    public List<RestaurantDto> getAllRestaurants() {
        return RestaurantUtils.createRestaurantDTOList(menuRepository.findAllByToday());
    }

    public RestaurantDto getRestaurantWithCurrentMenu(int id) {
        return RestaurantUtils.createRestaurantDTO(checkNotFound(menuRepository.findByTodayAndRestaurantId(id)));
    }

    public Restaurant getRestaurantWithMenus(int restaurantId) {
        return checkNotFound(restaurantRepository.findByIdWithMenus(restaurantId));
    }

    public Dish getDish(int dishId) {
        return checkNotFound(dishRepository.findById(dishId));
    }


    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Menu createMenu(int restaurantId, Menu menu) {
        Restaurant restaurant = checkNotFound(restaurantRepository.findByIdWithMenus(restaurantId));
        menu.setRestaurant(restaurant);
        return menuRepository.save(menu);
    }

    @Transactional
    public Dish createDish(int menuId, Dish dish) {
        Menu menu = checkNotFound(menuRepository.findById(menuId));
        dish.setMenu(menu);
        return  dishRepository.save(dish);
    }

    public void updateRestaurant(int id, Restaurant restaurant) {
        checkConsistentId(id, restaurant);
        restaurantRepository.save(restaurant);
    }

    @Transactional
    public void updateMenu(int restaurantId, int menuId, Menu menu) {
        Restaurant restaurant = checkNotFound(restaurantRepository.findById(restaurantId));
        checkConsistentId(menuId, menu);
        menu.setRestaurant(restaurant);
        menu.setVoteCount(0);
        menuRepository.save(menu);
    }

    @Transactional
    public void updateDish(int menuId, int dishId, Dish dish) {
        Menu menu = checkNotFound(menuRepository.findById(menuId));
        checkConsistentId(dishId, dish);
        menu.setVoteCount(0);
        menuRepository.save(menu);
        dish.setMenu(menu);
        dishRepository.save(dish);
    }

    public void deleteRestaurant(int id) {
        restaurantRepository.deleteById(id);
    }

    public void deleteMenu(int menuId) {
        menuRepository.deleteById(menuId);
    }

    public void deleteDish(int dishId) {
        dishRepository.deleteById(dishId);
    }
}
