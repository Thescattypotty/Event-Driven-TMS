package org.driventask.task.Controller;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    //flux functions
    @PostMapping
    public Mono<ResponseEntity<Void>> createTask(@RequestBody @Valid TaskRequest taskRequest){
        return taskService.createTask(taskRequest)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("createTask executed"))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
    }

    @GetMapping("/{taskId}")
    public Mono<ResponseEntity<TaskResponse>> getTaskById(@PathVariable String taskId) {
        return taskService.getTaskById(taskId)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("getTaskById executed task: " + next))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal))
            .map(taskResponse -> new ResponseEntity<>(taskResponse, HttpStatus.OK));
    }

    @GetMapping("/project/{projectId}")
    public Flux<TaskResponse> getAllTasks(@PathVariable("projectId") String projectId) {
        return taskService.getAllTasks(projectId)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("getAllTasks executed tasks: " + next))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal));
    }

    @PutMapping("/{taskId}")
    public Mono<ResponseEntity<Void>> updateTask(@PathVariable String taskId,@RequestBody @Valid TaskRequest taskRequest) {
        return taskService.updateTask(taskId, taskRequest)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("updateTask executed"))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED)));
    }

    @DeleteMapping("/{taskId}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String taskId) {
        return taskService.deleteTask(taskId)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("deleteTask executed"))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED)));
    }
    
    //socket functions !
    @MessageMapping("/task/create")
    public Mono<Void> createTaskWebSocket(@Valid TaskRequest taskRequest) {
        return taskService.createTask(taskRequest)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("createTask executed"))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal))
            .then();
    }

    @MessageMapping("/task/update/")
    public Mono<Void> updateTaskWebSocket(String taskId,TaskRequest taskRequest) {
        return taskService.updateTask(taskId, taskRequest)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("updateTaskWebSocket executed"))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal))
            .then();
    }

    @MessageMapping("/task/delete")
    public Mono<Void> deleteTaskWebSocket(String taskId) {
        return taskService.deleteTask(taskId)
            .doOnSubscribe(sub -> System.out.println("Subscription Started"))
            .doOnNext(next -> System.out.println("deleteTaskWebSocket executed"))
            .doOnError(e -> System.out.println("Error durring task Creation"))
            .doFinally(signal -> System.out.println("Processing completed with signal: " + signal))
            .then();
    }
}
