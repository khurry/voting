package ru.khurry.voting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "dishes")
public class Dish extends AbstractBaseEntity {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Min(1)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    @JsonIgnore
    private Menu menu;

    public Dish() {}

    public Dish(Integer id, String name, int price, Menu menu) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.menu = menu;
    }

    public Dish(Dish dish) {
        this.id = dish.id;
        this.name = dish.name;
        this.price = dish.price;
        this.menu = dish.menu;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
