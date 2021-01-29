package ru.khurry.voting.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.khurry.voting.model.Restaurant;
import ru.khurry.voting.util.NotFoundException;

import static ru.khurry.voting.web.testutils.RestaurantTestUtils.restaurant1;
import static ru.khurry.voting.web.testutils.RestaurantTestUtils.restaurant2;

@SuppressWarnings("ConstantConditions")
class RestaurantRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    RestaurantRepository repository;

    @Test
    void findAll() {
        Assertions.assertThat(repository.findAll())
                .usingElementComparatorIgnoringFields("menus")
                .containsExactly(restaurant1, restaurant2);
    }

    @Test
    void findByIdWithMenus() {
        Restaurant expected = restaurant1;
        Restaurant actual = repository.findByIdWithMenus(restaurant1.getId()).orElseThrow(NotFoundException::new);
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}