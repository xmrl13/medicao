package config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initializeDatabase(DatabaseClient client) {
        return args -> {
            client.sql("CREATE SCHEMA IF NOT EXISTS app;")
                    .then()
                    .then(client.sql("SET search_path TO app, public;").then())
                    .then(client.sql("""
                                                CREATE TABLE IF NOT EXISTS place_itens (
                            id SERIAL PRIMARY KEY,
                            place_name VARCHAR(255),
                            project_contract VARCHAR(255),
                            item_name VARCHAR(255),
                            item_unit VARCHAR(50),
                            predicted_value DECIMAL(19, 2),
                            accumulated_value DECIMAL(19, 2)
                                                );
                            """).then())
                    .doOnSuccess(unused -> System.out.println("Tabela 'projects' configurada com sucesso com índices!"))
                    .doOnError(error -> System.err.println("Erro ao configurar tabela 'projects': " + error.getMessage()))
                    .subscribe();
        };
    }
}
