package md.measurementplaceitem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"client", "controller", "dto", "exceptions", "model", "repository", "service"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
@EnableEurekaClient
public class MeasurementPlaceItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeasurementPlaceItemApplication.class, args);
    }

}
