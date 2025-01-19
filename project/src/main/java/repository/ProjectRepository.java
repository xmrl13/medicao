package repository;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import model.Project;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectRepository extends R2dbcRepository<Project, Long> {

    Mono<Object> findByNameAndContract(@NotBlank(message = "O nome da obra é obrigatório.") @Size(min = 5, message = "O nome da obra deve ter pelo menos 5 caracteres e comecar com SES") String name, @NotBlank(message = "O contrato é obrigatório.") String contract);

    Mono<Project> findByContract(@NotBlank(message = "O nome da obra é obrigatório.") @Size(min = 5, message = "O nome da obra deve ter pelo menos 5 caracteres e comecar com SES") String contract);
}
