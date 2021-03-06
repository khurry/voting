package ru.khurry.voting.util;

import org.springframework.lang.NonNull;
import ru.khurry.voting.model.AbstractBaseEntity;
import ru.khurry.voting.util.exception.IllegalRequestDataException;
import ru.khurry.voting.util.exception.NotFoundException;

import java.util.Optional;

public class ValidationUtils {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> T checkNotFound(Optional<T> optional) {
        return optional.orElseThrow(NotFoundException::new);
    }

    public static void checkConsistentId(int id, @NonNull AbstractBaseEntity entity) {
        if (entity.isNew()) {
            entity.setId(id);
        } else if (entity.getId() == null || entity.getId() != id) {
            throw new IllegalRequestDataException(entity + " must be with id=" + id);
        }
    }
}
