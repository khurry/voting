package ru.khurry.voting.util;

import org.springframework.lang.NonNull;
import ru.khurry.voting.model.AbstractBaseEntity;
import ru.khurry.voting.model.Restaurant;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class ValidationUtil {
    public static <T> T checkNotFound(Optional<T> object) {
        return object.orElseThrow(NotFoundException::new);
    }

    public static void checkIfNotBelongToRestaurant(int id, int dishRestaurantId) {
        if (id != dishRestaurantId) throw new NotFoundException();
    }

    public static void checkConsistentId(int id, @NonNull AbstractBaseEntity entity) {
        if (entity.isNew()) {
            entity.setId(id);
        } else if (entity.getId() == null || entity.getId() != id) {
            throw new IllegalRequestDataException(entity + " must be with id=" + id);
        }
    }
}
