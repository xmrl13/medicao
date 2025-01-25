package service;

import client.ProjectClient;
import dto.ProjectDTO;
import dto.ProjectRequestDTO;
import model.Project;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    private ProjectClient projectClient;

    public Mono<ResponseEntity<String>> createProject(ProjectDTO projectDTO, String token) {
        String action = "createProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {

                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
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

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
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

                    if (status == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == HttpStatus.FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != HttpStatus.OK) {
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return projectRepository.findByContract(projectRequestDTO.getContract())
                            .flatMap(existingProject -> {

                                // Verifica se o email já está no projeto
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

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
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

    public Mono<ResponseEntity<List<String>>> getContractsByUserEmail(String userEmail, String token) {

        String action = "getProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body(List.of("Ação não encontrada: " + action)));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body(List.of("Sem permissão para realizar essa ação")));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body(List.of("Erro ao verificar permissão: " + message)));
                    }

                    System.out.println(userEmail);

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
