package md.placeitem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@SpringBootApplication(scanBasePackages = {"client", "config", "controller", "dto", "exceptions", "model", "repository", "service"})
@EnableR2dbcRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
@EnableDiscoveryClient
public class PlaceItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaceItemApplication.class, args);
    }

}
