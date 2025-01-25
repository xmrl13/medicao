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
                        CREATE TABLE IF NOT EXISTS projects (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            contract VARCHAR(255) NOT NULL UNIQUE,
                            budget NUMERIC(30, 2) NOT NULL,
                            user_email TEXT[]
                        );
                        """).then())
                    .then(client.sql("CREATE INDEX IF NOT EXISTS idx_projects_name_contract ON projects(name, contract);").then())
                    .doOnSuccess(unused -> System.out.println("Tabela 'projects' configurada com sucesso com índices!"))
                    .doOnError(error -> System.err.println("Erro ao configurar tabela 'projects': " + error.getMessage()))
                    .subscribe();
        };
    }
}
