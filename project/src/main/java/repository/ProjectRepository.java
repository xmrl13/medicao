package repository;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import model.Project;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectRepository extends R2dbcRepository<Project, Long> {

    Mono<Object> findByNameAndContract(@NotBlank(message = "O nome da obra é obrigatório.") @Size(min = 5, message = "O nome da obra deve ter pelo menos 5 caracteres e comecar com SES") String name, @NotBlank(message = "O contrato é obrigatório.") String contract);

    Mono<Project> findByContract(@NotBlank(message = "O nome da obra é obrigatório.") @Size(min = 5, message = "O nome da obra deve ter pelo menos 5 caracteres e comecar com SES") String contract);


    @Query("SELECT * FROM projects WHERE :email = ANY(user_email)")
    Flux<Project> findByUserEmail(String email);

    @Modifying
    @Query("UPDATE projects SET user_email = array_append(user_email, :email) WHERE contract = :contract")
    Mono<Integer> addEmailToProject(@Param("email") String email, @Param("contract") String contract);

}
