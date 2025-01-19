package md.measurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = {"client", "config", "controller", "dto", "model", "repository", "service"})
@EntityScan(basePackages = "model")
@EnableR2dbcRepositories(basePackages = "repository")
@EnableDiscoveryClient
public class MeasurementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeasurementApplication.class, args);
    }

}
