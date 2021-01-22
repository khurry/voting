package ru.khurry.voting.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "restaurants")
public class Restaurant extends AbstractBaseEntity {
    @NotBlank
    @Size(max = 100)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Menu> menus;

    public Restaurant() {}

    public Restaurant(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Restaurant(Restaurant restaurant) {
        this.id = restaurant.id;
        this.name = restaurant.name;
        this.menus = restaurant.menus;
    }

    public String getName() {
        return name;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
//                ", menus=" + menus +
                '}';
    }
}
