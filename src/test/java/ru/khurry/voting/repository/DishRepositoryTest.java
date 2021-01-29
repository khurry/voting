package ru.khurry.voting.repository;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.khurry.voting.model.Dish;
import ru.khurry.voting.util.NotFoundException;
import ru.khurry.voting.web.testutils.RestaurantTestUtils;

class DishRepositoryTest extends AbstractRepositoryTest{
    @Autowired
    DishRepository repository;

    @SuppressWarnings("ConstantConditions")
    @Test
    void findByIdAndMenuId() {
        Dish expected = RestaurantTestUtils.dish1;
        Dish actual = repository.findByIdAndMenuId(expected.getMenu().getId(), expected.getId()).orElseThrow(NotFoundException::new);
        RecursiveComparisonConfiguration recursiveComparisonConfiguration = new RecursiveComparisonConfiguration();
        recursiveComparisonConfiguration.ignoreFields("menu");
        Assertions.assertThat(actual).usingRecursiveComparison(recursiveComparisonConfiguration).isEqualTo(expected);
    }

}