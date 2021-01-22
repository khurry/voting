package ru.khurry.voting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.khurry.voting.model.User;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<User, Integer> {
    @Override
    List<User> findAll();
}
