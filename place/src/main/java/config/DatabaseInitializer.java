package config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initializeDatabase(DatabaseClient client) {
        return args -> {
            client.sql("CREATE SCHEMA IF NOT EXISTS app")
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(count -> System.out.println("Schema 'app' criado com sucesso!"))
                    .then(client.sql("SET search_path TO app, public")
                            .fetch()
                            .rowsUpdated()
                            .doOnSuccess(count -> System.out.println("search_path configurado para 'app, public'"))
                    )
                    .then(client.sql("CREATE TABLE IF NOT EXISTS places (" +
                                    "id SERIAL PRIMARY KEY, " +
                                    "name VARCHAR(255) NOT NULL, " +
                                    "project_contract VARCHAR(255) NOT NULL" +
                                    ")").fetch()
                            .rowsUpdated()
                            .doOnSuccess(count -> System.out.println("Tabela 'itens' criada com sucesso!"))
                    )
                    .then(client.sql("CREATE UNIQUE INDEX IF NOT EXISTS idx_name_projct_contract ON places (name, project_contract)")
                            .fetch()
                            .rowsUpdated()
                            .doOnSuccess(count -> System.out.println("Índice único 'idx_name_unit' criado com sucesso!"))
                    )
                    .doOnError(error -> System.err.println("Erro ao inicializar o banco de dados: " + error.getMessage()))
                    .subscribe();
        };
    }
}
