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
                            CREATE TABLE IF NOT EXISTS measurements_places_itens (
                                id SERIAL PRIMARY KEY,
                                project_contract VARCHAR(255) NOT NULL,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                year_month VARCHAR(7) NOT NULL,
                                place_name VARCHAR(255) NOT NULL,
                                item_name VARCHAR(255) NOT NULL,
                                item_unit VARCHAR(50) NOT NULL,
                                predicted_value NUMERIC(15, 2),
                                accumulated_value NUMERIC(15, 2)
                            );
                            """).then())
                    .then(client.sql("CREATE INDEX IF NOT EXISTS idx_project_contract_year_month_place_item " +
                            "ON measurements_places_itens(project_contract, year_month, place_name, item_name);").then())
                    .doOnSuccess(unused -> System.out.println("Tabela 'measurements_places_itens' configurada com sucesso com índices!"))
                    .doOnError(error -> System.err.println("Erro ao configurar tabela 'measurements_places_itens': " + error.getMessage()))
                    .subscribe();
        };
    }
}
