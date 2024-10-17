package repository;

import model.User;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserCustomRepository {

    private final DatabaseClient databaseClient;

    @Autowired
    public UserCustomRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<User> findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = $1";

        return databaseClient.sql(query)
                .bind(0, email)
                .map((row, rowMetadata) -> new User(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("email", String.class)
                ))
                .one();
    }
}
