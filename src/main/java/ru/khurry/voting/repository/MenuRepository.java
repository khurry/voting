package ru.khurry.voting.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.khurry.voting.model.Menu;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface MenuRepository extends CrudRepository<Menu, Integer> {

    @Query(value = "SELECT * from MENUS WHERE created >= current_date ", nativeQuery = true)
    List<Menu> findAllByToday();

    @Query(value = "SELECT * from MENUS WHERE created >= current_date AND restaurant_id = ?1 ", nativeQuery = true)
    Optional<Menu> findByTodayAndRestaurantId(int restaurantId);

    @Transactional
    @Query(value = "update Menu m set m.voteCount = m.voteCount + 1 where m.id = ?1")
    @Modifying
    void incrementVoteCount(int id);

    @Transactional
    @Query(value = "update Menu m set m.voteCount = m.voteCount - 1 where m.id = ?1")
    @Modifying
    void decrementVoteCount(int id);
}
