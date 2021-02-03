package ru.khurry.voting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.khurry.voting.model.Restaurant;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RestaurantRepository extends CrudRepository<Restaurant, Integer> {
    @Override
    List<Restaurant> findAll();

    @EntityGraph(attributePaths = {"menus"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("FROM Restaurant r WHERE r.id=?1")
    Optional<Restaurant> findByIdWithMenus(int id);
}
