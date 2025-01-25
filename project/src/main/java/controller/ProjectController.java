package controller;

import dto.ProjectDTO;
import dto.ProjectRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createProject(@RequestBody ProjectDTO projectDTO, @RequestHeader("Authorization") String token) {
        return projectService.createProject(projectDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteProject(@RequestBody ProjectDTO projectDTO, @RequestHeader("Authorization") String token) {
        return projectService.deleteProject(projectDTO, token);
    }

    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existProject(@RequestBody ProjectRequestDTO projectRequestDTO, @RequestHeader("Authorization") String token) {
        return projectService.existsByContract(projectRequestDTO, token);
    }

    @GetMapping("/get-contracts-by-email")
    public Mono<ResponseEntity<List<String>>> getContractsByEmail(@RequestParam("userEmail") String userEmail, @RequestHeader("Authorization") String token) {
        return projectService.getContractsByUserEmail(userEmail, token);
    }

    @PostMapping("/add-email-in-project")
    public Mono<ResponseEntity<String>> addEmailInProject(@RequestBody ProjectRequestDTO projectRequestDTO, @RequestHeader("Authorization") String token) {
        return projectService.addEmailInProject(projectRequestDTO, token);
    }
    
}
