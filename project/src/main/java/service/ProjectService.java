package service;

import client.ProjectClient;
import dto.ProjectDTO;
import dto.ProjectRequestDTO;
import model.Project;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.ProjectRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.*;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectClient projectClient;

    public ProjectService(ProjectRepository projectRepository, ProjectClient projectClient) {
        this.projectRepository = projectRepository;
        this.projectClient = projectClient;
    }

    public Mono<ResponseEntity<String>> createProject(ProjectDTO projectDTO, String token) {
        String action = "createProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }

                    return projectRepository.findByNameAndContract(projectDTO.getName(), projectDTO.getContract())
                            .flatMap(existingProject ->
                                    Mono.just(ResponseEntity.status(CONFLICT)
                                            .body("A obra: " + projectDTO.getName() + " já existe para o contrato: " + projectDTO.getContract()))
                            )
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        Project project = new Project();
                                        project.setName(projectDTO.getName());
                                        project.setContract(projectDTO.getContract());
                                        project.setBudget(projectDTO.getBudget());
                                        return projectRepository.save(project)
                                                .map(savedProject -> ResponseEntity.status(CREATED)
                                                        .body("Obra criada com sucesso"));
                                    })
                            );
                });
    }

    public Mono<ResponseEntity<String>> deleteProject(ProjectDTO projectDTO, String token) {
        String action = "deletePlace";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }
                    return projectRepository.findByContract(projectDTO.getContract())
                            .flatMap(existingProject ->
                                    projectRepository.delete((Project) existingProject)
                                            .then(Mono.just(ResponseEntity.status(OK)
                                                    .body("Obra deletada com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NOT_FOUND)
                                    .body("Obra não encontrada com base no contrato fornecido")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a exclusão da obra: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> addEmailInProject(ProjectRequestDTO projectRequestDTO, String token) {
        String action = "addEmailInProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatusCode status = responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }

                    return projectRepository.findByContract(projectRequestDTO.getContract())
                            .flatMap(existingProject -> {

                                if (existingProject.getUserEmail() != null &&
                                        existingProject.getUserEmail().contains(projectRequestDTO.getUserEmail())) {
                                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                            .body("E-mail já existe no projeto."));
                                }

                                return projectRepository.addEmailToProject(projectRequestDTO.getUserEmail(), projectRequestDTO.getContract())
                                        .flatMap(rowsUpdated -> {
                                            if (rowsUpdated > 0) {
                                                return Mono.just(ResponseEntity.status(HttpStatus.OK)
                                                        .body("E-mail adicionado com sucesso ao projeto."));
                                            } else {
                                                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                        .body("Erro ao adicionar o e-mail. Nenhuma linha foi atualizada."));
                                            }
                                        });
                            })
                            .switchIfEmpty(
                                    Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                            .body("Projeto não encontrado com o contrato: " + projectRequestDTO.getContract()))
                            );
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao adicionar e-mail ao projeto: " + error.getMessage())));
    }


    public Mono<ResponseEntity<String>> existsByContract(ProjectRequestDTO projectRequestDTO, String token) {

        String action = "existProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }

                    return projectRepository.findByContract(projectRequestDTO.getContract())
                            .flatMap(existingProject ->
                                    Mono.just(ResponseEntity.status(OK)
                                            .body("Obra encontrada")))
                            .switchIfEmpty(
                                    Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body("Obra não encontrada")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar a existência da obra: " + error.getMessage())));
    }

    public Mono<ResponseEntity<?>> getContractsByUserEmail(String userEmail, String token) {

        String action = "getProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }

                    return projectRepository.findByUserEmail(userEmail)
                            .map(Project::getContract)
                            .collectList()
                            .flatMap(contracts -> {
                                if (contracts.isEmpty()) {
                                    return Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body(List.of("Nenhum contrato encontrado para o userEmail fornecido")));
                                }
                                return Mono.just(ResponseEntity.ok(contracts));
                            });
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(List.of("Erro ao buscar contratos: " + error.getMessage()))));
    }

}
