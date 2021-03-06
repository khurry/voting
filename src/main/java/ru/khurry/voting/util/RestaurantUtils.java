package ru.khurry.voting.util;

import ru.khurry.voting.dto.RestaurantDto;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantUtils {
    public static RestaurantDto createRestaurantDTO(Menu todayMenu) {
        return new RestaurantDto(todayMenu.getRestaurant(), todayMenu);
    }

    public static List<RestaurantDto> createRestaurantDTOList(List<Menu> todayMenus) {
        List<RestaurantDto> result = new ArrayList<>();

        for (Menu todayMenu : todayMenus) {
            Restaurant restaurant = Objects.requireNonNull(todayMenu.getRestaurant());
            result.add(new RestaurantDto(restaurant, todayMenu));
        }
        return result;
    }
}
