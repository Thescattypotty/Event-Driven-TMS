package org.driventask.task.Service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.UUID;

import org.driventask.task.Entity.Task;
import org.driventask.task.Enum.EPriority;
import org.driventask.task.Enum.EStatus;
import org.driventask.task.Exception.ProjectNotFoundException;
import org.driventask.task.Exception.TaskCreationException;
import org.driventask.task.Exception.TaskNotFoundException;
import org.driventask.task.Exception.TaskUpdateException;
import org.driventask.task.Exception.UserNotFoundException;
import org.driventask.task.FeignClient.ProjectClient;
import org.driventask.task.FeignClient.UserClient;
import org.driventask.task.KafkaService.TaskProducer;
import org.driventask.task.Payload.Mapper.TaskMapper;
import org.driventask.task.Payload.Request.TaskRequest;
import org.driventask.task.Payload.Response.TaskResponse;
import org.driventask.task.Repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@DisplayName("Task Service Tests")
@Feature("Task Service")
public class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskProducer taskProducer;

    @Mock
    private UserClient userClient;

    @Mock
    private ProjectClient projectClient;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Task - success")
    @Description("Test for successful task creation")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Task")
    void testCreateTaskSuccess() {
        TaskRequest request = new TaskRequest("Title", "Desc", EPriority.LOW, EStatus.TO_DO, "userId","projectId", null);
        when(projectClient.isProjectExist(any(String.class))).thenReturn(new ResponseEntity<>("true", HttpStatus.OK));
        when(userClient.isUserExist(any(String.class))).thenReturn(new ResponseEntity<>("true", HttpStatus.OK));
        when(taskMapper.toTask(any(TaskRequest.class))).thenReturn(new Task());
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(new Task()));

        taskService.createTask(request)
            .subscribe(
                unused -> assertTrue(true),
                ex -> fail("Should not fail on valid creation")
            );
        verify(taskProducer, times(0)).sendTaskCreationEvent(any());
    }

    @Test
    @DisplayName("Create Task - project not found")
    @Description("Test for task creation when project is not found")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create Task")
    void testCreateTaskProjectNotFound() {
        TaskRequest request = new TaskRequest("Title", "Desc", null, null, "userId", "projectId", null);
        when(projectClient.isProjectExist(any(String.class))).thenReturn(new ResponseEntity<>("false", HttpStatus.OK));

        taskService.createTask(request)
            .subscribe(
                unused -> fail("Should throw ProjectNotFoundException"),
                ex -> assertTrue(ex instanceof ProjectNotFoundException)
            );
        verify(taskProducer, times(0)).sendTaskCreationEvent(any());
    }

    @Test
    @DisplayName("Create Task - user not found")
    @Description("Test for task creation when user is not found")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create Task")
    void testCreateTaskUserNotFound() {
        TaskRequest request = new TaskRequest("Title", "Desc", null, null, "userId", "projectId", null);
        when(projectClient.isProjectExist(any(String.class))).thenReturn(new ResponseEntity<>("true", HttpStatus.OK));
        when(userClient.isUserExist(any(String.class))).thenReturn(new ResponseEntity<>("false", HttpStatus.OK));

        taskService.createTask(request)
            .subscribe(
                unused -> fail("Should throw UserNotFoundException"),
                ex -> assertTrue(ex instanceof UserNotFoundException)
            );
        verify(taskProducer, times(0)).sendTaskCreationEvent(any());
    }

    @Test
    @DisplayName("Create Task - exception on save")
    @Description("Test for task creation when there is an exception on save")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Task")
    void testCreateTaskSaveException() {
        TaskRequest request = new TaskRequest("Title", "Desc", null, null, "userId", "projectId", null);
        when(projectClient.isProjectExist(any(String.class))).thenReturn(new ResponseEntity<>("true", HttpStatus.OK));
        when(userClient.isUserExist(any(String.class))).thenReturn(new ResponseEntity<>("true", HttpStatus.OK));
        when(taskMapper.toTask(any(TaskRequest.class))).thenReturn(new Task());
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        taskService.createTask(request)
            .subscribe(
                unused -> fail("Should throw TaskCreationException"),
                ex -> assertTrue(ex instanceof TaskCreationException)
            );
        verify(taskProducer, times(0)).sendTaskCreationEvent(any());
    }

    @Test
    @DisplayName("Get Task by ID - success")
    @Description("Test for getting task by ID successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get Task")
    void testGetTaskByIdSuccess() {
        Task task = new Task();
        task.setTitle("My Task");
        when(taskRepository.findById(any(UUID.class))).thenReturn(Mono.just(task));
        when(taskMapper.fromTask(task)).thenReturn(new TaskResponse("id","Title", "Desc" , null, null, null, null, null, null, null, null, null));

        taskService.getTaskById(UUID.randomUUID().toString())
            .subscribe(
                response -> assertTrue(response.title().equals("My Task")),
                ex -> fail("Should not fail on existing task")
            );
    }

    @Test
    @DisplayName("Get Task by ID - not found")
    @Description("Test for getting task by ID when task is not found")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Task")
    void testGetTaskByIdNotFound() {
        when(taskRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        taskService.getTaskById(UUID.randomUUID().toString())
            .subscribe(
                unused -> fail("Should throw TaskNotFoundException"),
                ex -> assertTrue(ex instanceof TaskNotFoundException)
            );
    }

    @Test
    @DisplayName("Get All Tasks")
    @Description("Test for getting all tasks")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Task")
    void testGetAllTasks() {
        Task task = new Task();
        when(taskRepository.findByProjectId(any(String.class))).thenReturn(Flux.just(task));
        when(taskMapper.fromTask(any(Task.class))).thenReturn(new TaskResponse("id","Title", "Desc" , null, null, null, null, null, null, null, null, null));

        taskService.getAllTasks("projectId")
            .collectList()
            .subscribe(
                tasks -> assertTrue(tasks.size() == 1),
                ex -> fail("Should not fail on getting tasks")
            );
    }

    @Test
    @DisplayName("Update Task - success")
    @Description("Test for updating task successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Task")
    void testUpdateTaskSuccess() {
        Task existingTask = new Task();
        existingTask.setUserAssigned("oldUser");
        when(taskRepository.findById(any(UUID.class))).thenReturn(Mono.just(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(existingTask));

        taskService.updateTask(UUID.randomUUID().toString(),
            new TaskRequest("Title", "Desc", null, null, "userId", "projectId", null)
        ).subscribe(
            unused -> assertTrue(true),
            ex -> fail("Should not fail on valid update")
        );
        verify(taskProducer, times(0)).sendTaskUpdateEvent(any());
    }

    @Test
    @DisplayName("Update Task - not found")
    @Description("Test for updating task when task is not found")
    @Severity(SeverityLevel.NORMAL)
    @Story("Update Task")
    void testUpdateTaskNotFound() {
        when(taskRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        taskService.updateTask(UUID.randomUUID().toString(),
            new TaskRequest("Title", "Desc", null, null, "userId", "projectId", null)
        ).subscribe(
            unused -> fail("Should throw TaskNotFoundException"),
            ex -> assertTrue(ex instanceof TaskNotFoundException)
        );
        verify(taskProducer, times(0)).sendTaskUpdateEvent(any());
    }

    @Test
    @DisplayName("Update Task - exception on save")
    @Description("Test for updating task when there is an exception on save")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Task")
    void testUpdateTaskSaveException() {
        Task existingTask = new Task();
        when(taskRepository.findById(any(UUID.class))).thenReturn(Mono.just(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        taskService.updateTask(UUID.randomUUID().toString(),
            new TaskRequest("Title", "Desc", null, null, "userId", "projectId", null)
        ).subscribe(
            unused -> fail("Should throw TaskUpdateException"),
            ex -> assertTrue(ex instanceof TaskUpdateException)
        );
        verify(taskProducer, times(0)).sendTaskUpdateEvent(any());
    }

    @Test
    @DisplayName("Delete Task - success")
    @Description("Test for deleting task successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Task")
    void testDeleteTaskSuccess() {
        Task task = new Task();
        when(taskRepository.findById(any(UUID.class))).thenReturn(Mono.just(task));
        when(taskRepository.delete(any(Task.class))).thenReturn(Mono.empty());

        taskService.deleteTask(UUID.randomUUID().toString())
            .subscribe(
                unused -> assertTrue(true),
                ex -> fail("Should not fail on existing task deletion")
            );
    }

    @Test
    @DisplayName("Delete Task - not found")
    @Description("Test for deleting task when task is not found")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete Task")
    void testDeleteTaskNotFound() {
        when(taskRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        taskService.deleteTask(UUID.randomUUID().toString())
            .subscribe(
                unused -> fail("Should throw TaskNotFoundException"),
                ex -> assertTrue(ex instanceof TaskNotFoundException)
            );
    }
}
