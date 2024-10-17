package project.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import project.dto.ProjectDTO;
import project.model.Project;
import project.repository.ProjectRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {


        if (projectDTO.getName().replaceAll("\\s", "").length() <= 4) {
            throw new IllegalArgumentException("Nome da obra deve começar com 'SES', não pode conter caracteres especiais e deve ter ao menos 4 caracteres");
        }

        projectRepository.findByContract(projectDTO.getContract()).ifPresent(obra -> {
            throw new IllegalArgumentException("Já existe uma obra cadastrada com esse contrato");
        });

        Project novaObra = new Project(projectDTO.getName(), projectDTO.getContract(), projectDTO.getBudget());
        projectRepository.save(novaObra);

        return new ProjectDTO(novaObra.getName(), novaObra.getContract(), novaObra.getBudget());
    }

    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projects.parallelStream().map(obra -> new ProjectDTO(obra.getName(), obra.getContract(), obra.getBudget())).collect(Collectors.toList());
    }

    public ProjectDTO findByContract(String contrato) {
        Project projectFound = projectRepository.findByContract(contrato).orElseThrow(() -> new NoSuchElementException(String.format("Contrato: %s não encontrado", contrato)));
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setContract(projectFound.getContract());
        projectDTO.setName(projectFound.getName());
        projectDTO.setBudget(projectFound.getBudget());
        return projectDTO;
    }

    public boolean projectExistsByContract(String contrato) {
        return projectRepository.existsByContract(contrato);
    }
}
