package org.driventask.project.Controller;

import org.junit.jupiter.api.DisplayName;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;
import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import org.driventask.project.Service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Project Controller Test")
@Feature("Project")
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = bindToController(projectController).build();
    }

    @Test
    @Description("Test for getting a project by ID successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get Project by ID")
    @DisplayName("Get Project by ID - Success")
    void testGetProjectByIdSuccess() {
        ProjectResponse projectResponse = new ProjectResponse("1", "Test Project", "Desc", null, null, null, null, null,  null, null);
        when(projectService.getProjectById(anyString())).thenReturn(Mono.just(projectResponse));

        webTestClient.get().uri("/api/v1/project/{projectId}", "1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(ProjectResponse.class)
            .value(response -> assertEquals("Test Project", response.name()));
    }

    @Test
    @Description("Test for getting all projects successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get All Projects")
    @DisplayName("Get All Projects - Success")
    void testGetAllProjectsSuccess() {
        ProjectResponse projectResponse = new ProjectResponse("1", "Test Project", "Desc", null, null, null, null, null,  null, null);
        when(projectService.getAllProjects(anyString())).thenReturn(Flux.just(projectResponse));

        webTestClient.get().uri("/api/v1/project/user/{userId}", "userIdTest")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ProjectResponse.class)
            .value(response -> assertFalse(response.isEmpty()));
    }

    @Test
    @Description("Test for creating a project successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Project")
    @DisplayName("Create Project - Success")
    void testCreateProjectSuccess() {
        when(projectService.createProject(any(ProjectRequest.class))).thenReturn(Mono.empty());

        ProjectRequest request = new ProjectRequest("newProject", "desc", null, null, null, null);
        webTestClient.post().uri("/api/v1/project")
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    @Description("Test for updating a project successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Project")
    @DisplayName("Update Project - Success")
    void testUpdateProjectSuccess() {
        when(projectService.updateProject(anyString(), any(ProjectRequest.class))).thenReturn(Mono.empty());
        ProjectRequest request = new ProjectRequest("updatedName", "desc", null, null, null, null);

        webTestClient.put().uri("/api/v1/project/{projectId}", "1")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Description("Test for deleting a project successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Project")
    @DisplayName("Delete Project - Success")
    void testDeleteProjectSuccess() {
        when(projectService.deleteProject(anyString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/project/{projectId}", "1")
            .exchange()
            .expectStatus().isNoContent();
    }
}
