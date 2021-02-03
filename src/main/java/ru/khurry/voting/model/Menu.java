package ru.khurry.voting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "menus")
public class Menu extends AbstractBaseEntity {
    @NotNull
    private LocalDate created;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "menu")
    private List<Dish> dishes;

    @Column(name = "votecount")
    private int voteCount;

    public Menu() {}

    public Menu(Integer id, LocalDate created, Restaurant restaurant, int voteCount) {
        this.id = id;
        this.created = created;
        this.restaurant = restaurant;
        this.voteCount = voteCount;
    }

    public Menu(Menu menu) {
        this.id = menu.id;
        this.created = menu.created;
        this.restaurant = menu.restaurant;
        this.voteCount = menu.voteCount;
        this.dishes = menu.dishes;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public LocalDate getCreated() {
        return created;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void incrementVoteCount() {
        voteCount++;
    }

    public void decrementVoteCount() {
        if(voteCount <= 0) throw new IllegalStateException("votecount cannot be decremented because it's zero or less");
        voteCount--;
    }

}
