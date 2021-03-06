package ru.khurry.voting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.khurry.voting.model.Dish;

@Repository
@Transactional(readOnly = true)
public interface DishRepository extends CrudRepository<Dish, Integer> {
}
