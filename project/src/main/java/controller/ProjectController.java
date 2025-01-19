package controller;

import dto.ProjectDTO;
import dto.ProjectRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.ProjectService;

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

}
