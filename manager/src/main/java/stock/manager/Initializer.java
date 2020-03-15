package stock.manager;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import stock.manager.model.Product;
import stock.manager.model.ProductRepository;

import java.util.stream.Stream;

@Component
public class Initializer implements CommandLineRunner {

    private final ProductRepository repository;

    public Initializer(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {
        Stream.of("Apple", "Banana", "Strawberry").forEach(name ->
                repository.save(new Product(name))
        );
    }
}