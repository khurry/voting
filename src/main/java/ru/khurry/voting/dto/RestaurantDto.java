package ru.khurry.voting.dto;

import org.springframework.lang.NonNull;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;

public class RestaurantDto {
    private Integer id;
    private String name;
    private Menu todayMenu;

    public RestaurantDto() {}

    public RestaurantDto(@NonNull Restaurant restaurant, Menu todayMenu) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.todayMenu = todayMenu;
    }

    public Menu getTodayMenu() {
        return todayMenu;
    }

    public void setTodayMenu(Menu todayMenu) {
        this.todayMenu = todayMenu;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", todayMenu=" + todayMenu +
                '}';
    }
}
