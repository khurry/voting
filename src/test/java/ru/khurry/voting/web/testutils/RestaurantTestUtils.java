package ru.khurry.voting.web.testutils;

import ru.khurry.voting.model.Dish;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;

import java.time.LocalDate;
import java.util.Arrays;

public class RestaurantTestUtils {
    public static final Restaurant restaurant1 = new Restaurant(100000, "Union Bar");
    public static final Restaurant restaurant2 = new Restaurant(100001, "Restomarket");

    public static final Menu menu1 = new Menu(100002, LocalDate.now(), restaurant1, 0);
    public static final Menu menu2 = new Menu(100003, LocalDate.of(2020, 12, 21), restaurant1, 0);
    public static final Menu menu3 = new Menu(100004, LocalDate.now(), restaurant2, 0);

    public static final Dish dish1 = new Dish(100005, "Cesar Salad", 2500, menu1);
    public static final Dish dish2 = new Dish(100006, "Cheese Soup", 3000, menu1);
    public static final Dish dish3 = new Dish(100007, "Fresh salad", 1500, menu2);
    public static final Dish dish4 = new Dish(100008, "Borsch", 1600, menu2);
    public static final Dish dish5 = new Dish(100009, "Rice with chicken", 1800, menu2);
    public static final Dish dish6 = new Dish(100010, "Salad with chicken", 3000, menu3);
    public static final Dish dish7 = new Dish(100011, "Burger", 3100, menu3);

    static {
        restaurant1.setMenus(Arrays.asList(menu2, menu1));
        restaurant2.setMenus(Arrays.asList(menu3));

        menu1.setDishes(Arrays.asList(dish1, dish2));
        menu2.setDishes(Arrays.asList(dish3, dish4, dish5));
        menu3.setDishes(Arrays.asList(dish6, dish7));
    }
}
