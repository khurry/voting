package ru.khurry.voting.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ru.khurry.voting.model.Menu;

import java.util.List;

import static ru.khurry.voting.web.testdata.RestaurantTestData.*;

class MenuRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    MenuRepository repository;

    @Test
    void findById() {
        Assertions.assertThat(repository.findById(menu1.getId()).orElseThrow())
                .usingRecursiveComparison().ignoringFields("restaurant.menus", "dishes.menu")
                .isEqualTo(menu1);
    }

    @Test
    void findAllByToday() {
        List<Menu> actual = repository.findAllByToday();
        Assertions.assertThat(actual)
                .usingElementComparatorIgnoringFields("dishes", "restaurant.menu")
                .containsExactlyInAnyOrder(menu1, menu3);
    }

    @Test
    void findByTodayAndRestaurantId() {
        Menu expected = menu1;
        Menu actual = repository.findByTodayAndRestaurantId(menu1.getRestaurant().getId()).orElseThrow();

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("restaurant.menus", "dishes.menu")
                .isEqualTo(expected);
    }


    @Test
    void incrementVoteCount() {
        repository.incrementVoteCount(menu1.getId());
        int expected = 1;
        int actual = repository.findById(menu1.getId()).orElseThrow().getVoteCount();
        Assertions.assertThat(expected).isEqualTo(actual);
    }

    @Test
    void decrementVoteCount() {
        int expected = 2;
        for (int i = 0; i < 3; i++) {
            repository.incrementVoteCount(menu1.getId());
        }
        repository.decrementVoteCount(menu1.getId());
        int actual = repository.findById(menu1.getId()).orElseThrow().getVoteCount();
        Assertions.assertThat(expected).isEqualTo(actual);
    }

    @Test
    void decrementZeroVoteCount() {
        Assertions.assertThatThrownBy(() -> repository.decrementVoteCount(menu1.getId())).isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

}