package org.driventask.project.Service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.driventask.project.Entity.Project;
import org.driventask.project.Exception.ProjectNotFoundException;
import org.driventask.project.KafkaService.ProjectProducer;
import org.driventask.project.Payload.Mapper.ProjectMapper;
import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@DisplayName("Project Service Test")
@Feature("Project")
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectProducer projectProducer;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Project with success")
    @Description("Given ProjectRequest, When createProject, Then return Mono<Void>")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Project")
    void testCreateProjectWithSuccess(){
        ProjectRequest projectRequest = new ProjectRequest("cc", "des", LocalDateTime.now(), LocalDateTime.now(), null, null);
        
        projectService.createProject(projectRequest)
            .subscribe(
                unused -> assertTrue(true),
                ex -> fail("Should not fail")
            );
    }

    @Test
    @DisplayName("Create Project with failure")
    @Description("Given invalid ProjectRequest, When createProject, Then return error")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Project")
    void testCreateProjectWithFail(){
        ProjectRequest projectRequest = new ProjectRequest(null, "des", LocalDateTime.now(), LocalDateTime.now(), null, null);
        
        projectService.createProject(projectRequest)
            .subscribe(
                unused -> assertTrue(false),
                ex -> fail("Should not fail")
            );
    }

    @Test
    @DisplayName("Update Project with success")
    @Description("Given valid ProjectRequest and existing project, When updateProject, Then return Mono<Void>")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Project")
    void testProjectUpdateWithSuccess(){
        Project project = new Project();
        project.setName("yahya");
        when(projectRepository.findById(any(UUID.class))).thenReturn(Mono.just(project));
        when(projectRepository.save(any(Project.class))).thenReturn(Mono.just(project));

        ProjectRequest projectRequest = new ProjectRequest("tatak", "des", LocalDateTime.now(), LocalDateTime.now(), null, null);
        projectService.updateProject(UUID.randomUUID().toString(), projectRequest)
            .subscribe(
                unused -> assertTrue(true),
                ex -> fail("Should not fail")
            );
    }

    @Test
    @DisplayName("Update Project with failure")
    @Description("Given invalid ProjectRequest or non-existing project, When updateProject, Then return error")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Project")
    void testProjectUpdateWithFail(){
        Project project = new Project();
        project.setName("yahya");
        when(projectRepository.save(any(Project.class))).thenReturn(Mono.just(project));
        when(projectRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        ProjectRequest projectRequest = new ProjectRequest("tatak", "des", LocalDateTime.now(), LocalDateTime.now(), null, null);
        projectService.updateProject(UUID.randomUUID().toString(), projectRequest)
            .subscribe(
                result -> fail("Should throw ProjectNotFoundException"),
                ex -> assertTrue(ex instanceof ProjectNotFoundException)
            );
    }
    
    @Test
    @DisplayName("Get Project by ID - success")
    @Description("Test retrieving a project by ID when it exists")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get project by ID")
    void testGetProjectByIdSuccess() {
        Project project = new Project();
        project.setName("Existing Project");
        when(projectRepository.findById(any(UUID.class))).thenReturn(Mono.just(project));

        projectService.getProjectById(UUID.randomUUID().toString())
            .subscribe(
                result -> assertTrue(result.name().equals("Existing Project")),
                ex -> fail("Should not fail")
            );
    }

    @Test
    @DisplayName("Get Project by ID - not found")
    @Description("Test retrieving a project by ID when it doesn't exist")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get project by ID")
    void testGetProjectByIdNotFound() {
        when(projectRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        projectService.getProjectById(UUID.randomUUID().toString())
            .subscribe(
                result -> fail("Should throw ProjectNotFoundException"),
                ex -> assertTrue(ex instanceof ProjectNotFoundException)
            );
    }

    @Test
    @DisplayName("Delete Project - success")
    @Description("Test deleting a project that exists")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete project")
    void testDeleteProjectSuccess() {
        Project project = new Project();
        when(projectRepository.findById(any(UUID.class))).thenReturn(Mono.just(project));
        when(projectRepository.delete(any(Project.class))).thenReturn(Mono.empty());

        projectService.deleteProject(UUID.randomUUID().toString())
            .subscribe(
                unused -> assertTrue(true),
                ex -> fail("Should not fail")
            );
    }

    @Test
    @DisplayName("Delete Project - not found")
    @Description("Test deleting a project that doesn't exist")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete project")
    void testDeleteProjectNotFound() {
        when(projectRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        projectService.deleteProject(UUID.randomUUID().toString())
            .subscribe(
                unused -> fail("Should throw ProjectNotFoundException"),
                ex -> assertTrue(ex instanceof ProjectNotFoundException)
            );
    }

    @Test
    @DisplayName("Get All Projects - not empty")
    @Description("Test retrieving all projects when list is not empty")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get all projects")
    void testGetAllProjectsNotEmpty() {
        Project project = new Project();
        when(projectRepository.findByUserIdInFlux(any(String.class))).thenReturn(Flux.just(project));
        projectService.getAllProjects(anyString())
            .collectList()
            .subscribe(
                projects -> assertTrue(!projects.isEmpty()),
                ex -> fail("Should not fail")
            );
    }

    @Test
    @DisplayName("Get All Projects - empty")
    @Description("Test retrieving all projects when list is empty")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get all projects")
    void testGetAllProjectsEmpty() {
        when(projectRepository.findByUserIdInFlux(any(String.class))).thenReturn(Flux.empty());

        projectService.getAllProjects(anyString())
            .collectList()
            .subscribe(
                projects -> assertTrue(projects.isEmpty()),
                ex -> fail("Should not fail")
            );
    }

}
