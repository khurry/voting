package ru.khurry.voting.repository;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.khurry.voting.model.Dish;
import ru.khurry.voting.web.testdata.RestaurantTestData;

class DishRepositoryTest extends AbstractRepositoryTest{
    @Autowired
    DishRepository repository;

    @Test
    void findByIdAndMenuId() {
        Dish expected = RestaurantTestData.dish1;
        Dish actual = repository.findByIdAndMenuId(expected.getMenu().getId(), expected.getId()).orElseThrow();
        RecursiveComparisonConfiguration recursiveComparisonConfiguration = new RecursiveComparisonConfiguration();
        recursiveComparisonConfiguration.ignoreFields("menu");
        Assertions.assertThat(actual).usingRecursiveComparison(recursiveComparisonConfiguration).isEqualTo(expected);
    }

}