package org.driventask.task.IService;

import org.driventask.task.Payload.Request.TaskRequest;
import org.driventask.task.Payload.Response.TaskResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITaskService {
    Mono<Void> createTask(TaskRequest taskRequest);
    Mono<TaskResponse> getTaskById(String taskId);
    Flux<TaskResponse> getAllTasks(String projectId);
    Mono<Void> updateTask(String taskId , TaskRequest taskRequest);
    Mono<Void> deleteTask(String taskId);
}
