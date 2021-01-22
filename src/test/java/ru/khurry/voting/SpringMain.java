package ru.khurry.voting;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.khurry.voting.model.Restaurant;
import ru.khurry.voting.repository.RestaurantRepository;
import java.util.Arrays;
import java.util.List;

public class SpringMain {
    public static void main(String[] args) {
/*        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml", "spring/spring-db.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            RestaurantRepository repository = appCtx.getBean(RestaurantRepository.class);

            Restaurant restaurant = repository.findById(100000).orElse(null);
            List<Restaurant> rests = repository.findAll();
            for (Restaurant rest : rests) {
                System.out.println(rest.getDishes());
            }
        }*/
    }
}
