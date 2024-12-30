package org.driventask.task.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.driventask.task.Payload.Request.TaskRequest;
import org.driventask.task.Payload.Response.TaskResponse;
import org.driventask.task.Enum.EPriority;
import org.driventask.task.Enum.EStatus;
import org.driventask.task.Exception.ProjectNotFoundException;
import org.driventask.task.Exception.TaskCreationException;
import org.driventask.task.Exception.TaskNotFoundException;
import org.driventask.task.Exception.TaskUpdateException;
import org.driventask.task.ExceptionHandler.GlobalExceptionHandler;
import org.driventask.task.Service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@DisplayName("Task Controller Test")
@Feature("Task Controller")
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private TaskController taskController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = bindToController(taskController)
            .controllerAdvice(globalExceptionHandler)
            .build();
    }

    @Test
    @DisplayName("Create Task - success")
    @Description("Test for successfully creating a task")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Task")
    void testCreateTaskSuccess() {
        when(taskService.createTask(new TaskRequest("Title","Desc",EPriority.LOW,EStatus.TO_DO,"user","proj",null)))
            .thenReturn(Mono.empty());

        webTestClient.post().uri("/api/v1/task")
            .bodyValue(new TaskRequest("Title","Desc",EPriority.LOW,EStatus.TO_DO,"user","proj",null))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Create Task - invalid data")
    @Description("Test for handling invalid data when creating a task")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create Task")
    void testCreateTaskInvalidData() {
        webTestClient.post().uri("/api/v1/task")
            .bodyValue(new TaskRequest("", "", null, null, null, "", null))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Create Task - project not found")
    @Description("Test for handling project not found error when creating a task")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create Task")
    void testCreateTaskProjectNotFound() {
        when(taskService.createTask(any(TaskRequest.class)))
            .thenThrow(new ProjectNotFoundException("Project not found"));
        webTestClient.post().uri("/api/v1/task")
            .bodyValue(new TaskRequest("Title", "Description", EPriority.LOW, EStatus.TO_DO, "user", "noProj", null))
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    @DisplayName("Create Task - creation error")
    @Description("Test for handling creation error when creating a task")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Task")
    void testCreateTaskCreationError() {
        when(taskService.createTask(any(TaskRequest.class)))
            .thenReturn(Mono.error(new TaskCreationException("Creation failed")));
        webTestClient.post().uri("/api/v1/task")
            .bodyValue(new TaskRequest("Title", "Description", EPriority.LOW, EStatus.TO_DO, "user", "proj", null))
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Get Task by ID - success")
    @Description("Test for successfully retrieving a task by ID")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Task by ID")
    void testGetTaskByIdSuccess() {
        TaskResponse response = new TaskResponse(
            "id","Title","Desc",null,EPriority.LOW,EStatus.TO_DO,
            null,null,"user","proj",null,null
        );
        when(taskService.getTaskById("1234")).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/task/{taskId}", "1234")
            .exchange()
            .expectStatus().isOk()
            .expectBody(TaskResponse.class)
            .value(res -> assertEquals("Title", res.title()));
    }

    @Test
    @DisplayName("Get All Tasks - success")
    @Description("Test for successfully retrieving all tasks for a project")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get All Tasks")
    void testGetAllTasksSuccess() {
        TaskResponse taskResp = new TaskResponse(
            "id","Title","Desc",null,EPriority.LOW,EStatus.TO_DO,
            null,null,"user","proj",null,null
        );
        when(taskService.getAllTasks("projectA")).thenReturn(Flux.just(taskResp));

        webTestClient.get().uri("/api/v1/task/project/{projectId}", "projectA")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(TaskResponse.class)
            .value(list -> assertEquals(1, list.size()));
    }

    @Test
    @DisplayName("Get All Tasks - empty list")
    @Description("Test for handling empty task list for a project")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get All Tasks")
    void testGetAllTasksEmpty() {
        when(taskService.getAllTasks("emptyProject")).thenReturn(Flux.empty());
        webTestClient.get().uri("/api/v1/task/project/{projectId}", "emptyProject")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(TaskResponse.class)
            .hasSize(0);
    }

    @Test
    @DisplayName("Update Task - success")
    @Description("Test for successfully updating a task")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Task")
    void testUpdateTaskSuccess() {
        when(taskService.updateTask("1234", new TaskRequest("Title","Desc",null,null,"user","proj",null)))
            .thenReturn(Mono.empty());

        webTestClient.put().uri("/api/v1/task/{taskId}", "1234")
            .bodyValue(new TaskRequest("Title","Desc",null,null,"user","proj",null))
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Update Task - not found")
    @Description("Test for handling task not found error when updating a task")
    @Severity(SeverityLevel.NORMAL)
    @Story("Update Task")
    void testUpdateTaskNotFound() {
        when(taskService.updateTask("notFound", new TaskRequest("Title","Desc",null,null,"user","proj",null)))
            .thenThrow(new TaskNotFoundException("ID not found"));
        webTestClient.put().uri("/api/v1/task/{taskId}", "notFound")
            .bodyValue(new TaskRequest("Title","Desc",null,null,"user","proj",null))
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Update Task - exception on update")
    @Description("Test for handling exception during task update")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Task")
    void testUpdateTaskUpdateException() {
        when(taskService.updateTask(eq("updFail"), any(TaskRequest.class)))
            .thenReturn(Mono.error(new TaskUpdateException("Update failed")));
        webTestClient.put().uri("/api/v1/task/{taskId}", "updFail")
            .bodyValue(new TaskRequest("Title", "Desc", null, null, "user", "proj", null))
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Delete Task - success")
    @Description("Test for successfully deleting a task")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Task")
    void testDeleteTaskSuccess() {
        when(taskService.deleteTask("1234")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/task/{taskId}", "1234")
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Delete Task - not found")
    @Description("Test for handling task not found error when deleting a task")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete Task")
    void testDeleteTaskNotFound() {
        when(taskService.deleteTask("notFound"))
            .thenThrow(new TaskNotFoundException("ID not found"));
        webTestClient.delete().uri("/api/v1/task/{taskId}", "notFound")
            .exchange()
            .expectStatus().isNotFound();
    }
    
}
