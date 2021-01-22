package ru.khurry.voting.dto;

import org.springframework.lang.NonNull;
import ru.khurry.voting.model.Dish;
import ru.khurry.voting.model.Menu;
import ru.khurry.voting.model.Restaurant;
import ru.khurry.voting.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RestaurantDTO {
    private Integer id;
    private String name;
    private Menu todayMenu;

    public RestaurantDTO() {}

    public RestaurantDTO(@NonNull Restaurant restaurant, Menu todayMenu) {
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
