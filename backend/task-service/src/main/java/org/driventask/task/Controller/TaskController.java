package org.driventask.task.Controller;

import java.util.List;

import org.driventask.task.Payload.Request.TaskRequest;
import org.driventask.task.Payload.Response.TaskResponse;
import org.driventask.task.Service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Mono<ResponseEntity<Void>> createTask(@RequestBody @Valid TaskRequest taskRequest){
        return taskService.createTask(taskRequest)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("createTask executed"))
            .doOnError(e -> log.error("Error durring task Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
    }

    @GetMapping("/{taskId}")
    public Mono<ResponseEntity<TaskResponse>> getTaskById(@PathVariable String taskId) {
        return taskService.getTaskById(taskId)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("getTaskById executed task: " + next))
            .doOnError(e -> log.error("Error durring task Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .map(taskResponse -> new ResponseEntity<>(taskResponse, HttpStatus.OK));
    }

    @GetMapping("/project/{projectId}")
    public Mono<ResponseEntity<List<TaskResponse>>> getAllTasks(@PathVariable("projectId") String projectId) {
        return taskService.getAllTasks(projectId)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("getAllTasks executed tasks: " + next))
            .doOnError(e -> log.error("Error durring task Creation"))
            .collectList()
            .map(taskResponse -> new ResponseEntity<>(taskResponse, HttpStatus.OK))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal));
    }

    @PutMapping("/{taskId}")
    public Mono<ResponseEntity<Void>> updateTask(@PathVariable String taskId,@RequestBody @Valid TaskRequest taskRequest) {
        return taskService.updateTask(taskId, taskRequest)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("updateTask executed"))
            .doOnError(e -> log.error("Error durring task Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
    }

    @DeleteMapping("/{taskId}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String taskId) {
        return taskService.deleteTask(taskId)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("deleteTask executed"))
            .doOnError(e -> log.error("Error durring task Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }
    
    @MessageMapping("/task/create")
    public Mono<Void> createTaskWebSocket(@Valid TaskRequest taskRequest) {
        return taskService.createTask(taskRequest)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("createTask executed"))
            .doOnError(e -> log.error("Error durring task Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then();
    }

    @MessageMapping("/task/update/")
    public Mono<Void> updateTaskWebSocket(String taskId,TaskRequest taskRequest) {
        return taskService.updateTask(taskId, taskRequest)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("updateTaskWebSocket executed"))
            .doOnError(e -> log.error("Error durring task Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then();
    }

    @MessageMapping("/task/delete")
    public Mono<Void> deleteTaskWebSocket(String taskId) {
        return taskService.deleteTask(taskId)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("deleteTaskWebSocket executed"))
            .doOnError(e -> log.error("Error durring task Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then();
    }
}
