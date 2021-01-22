package ru.khurry.voting.util;

import ru.khurry.voting.dto.RestaurantDTO;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantUtil {
    public static RestaurantDTO createRestaurantDTO(Menu todayMenu) {
        return new RestaurantDTO(todayMenu.getRestaurant(), todayMenu);
    }

    public static List<RestaurantDTO> createRestaurantDTOList(List<Menu> todayMenus) {
        List<RestaurantDTO> result = new ArrayList<>();

        for (Menu todayMenu : todayMenus) {
            Restaurant restaurant = todayMenu.getRestaurant();
            if (restaurant != null) result.add(new RestaurantDTO(restaurant, todayMenu));
        }
        return result;
    }
}
