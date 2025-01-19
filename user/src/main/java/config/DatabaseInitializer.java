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
                        CREATE TABLE IF NOT EXISTS users (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            password VARCHAR(255) NOT NULL,
                            role VARCHAR(50),
                            secret_phrase VARCHAR(255) NOT NULL CHECK (char_length(secret_phrase) >= 6)
                        );
                        """).then())
                    .then(client.sql("CREATE INDEX IF NOT EXISTS idx_users_name ON users(name);").then())
                    .then(client.sql("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);").then())
                    .then(client.sql("CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);").then())
                    .doOnSuccess(unused -> System.out.println("Tabela 'users' configurada com sucesso com índices!"))
                    .doOnError(error -> System.err.println("Erro ao configurar tabela 'users': " + error.getMessage()))
                    .subscribe();
        };
    }
}
