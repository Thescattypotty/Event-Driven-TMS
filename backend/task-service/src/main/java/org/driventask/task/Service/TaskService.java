package org.driventask.task.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import org.driventask.task.Exception.ProjectNotFoundException;
import org.driventask.task.Exception.TaskCreationException;
import org.driventask.task.Exception.TaskNotFoundException;
import org.driventask.task.Exception.TaskUpdateException;
import org.driventask.task.Exception.UserNotFoundException;
import org.driventask.task.FeignClient.ProjectClient;
import org.driventask.task.FeignClient.UserClient;
import org.driventask.task.IService.ITaskService;
import org.driventask.task.KafkaService.TaskProducer;
import org.driventask.task.Payload.Kafka.TaskCreation;
import org.driventask.task.Payload.Kafka.TaskUpdate;
import org.driventask.task.Payload.Mapper.TaskMapper;
import org.driventask.task.Payload.Request.TaskRequest;
import org.driventask.task.Payload.Response.TaskResponse;
import org.driventask.task.Repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService implements ITaskService{
    
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;
    private final UserClient userClient;
    private final ProjectClient projectClient;

    @Override
    @Transactional
    public Mono<Void> createTask(TaskRequest taskRequest) {
        return Mono.fromCallable(() -> {
            if(!checkForProjectExistance(taskRequest.projectId())){
                return Mono.error(new ProjectNotFoundException("Project not found with ID: " + taskRequest.projectId()));
            }
            if(StringUtils.hasText(taskRequest.userAssigned()) && !checkForUserExistance(taskRequest.userAssigned())){
                return Mono.error(new UserNotFoundException("User not found with ID: " + taskRequest.userAssigned()));
            }
            return Mono.empty();
        })
        .flatMap(nullValue -> taskRepository.save(taskMapper.toTask(taskRequest)))
        .doOnNext(taskCreated -> {
            taskProducer.sendTaskCreationEvent(
                    new TaskCreation(
                        taskCreated.getId().toString(),
                        taskCreated.getTitle(),
                        taskCreated.getProjectId(),
                        taskCreated.getUserAssigned(),
                        taskCreated.getCreatedAt()
                    )
                );
        })
        .then()
        .onErrorMap(e -> new TaskCreationException("Cannot create Task"));
    }
    
    @Override
    public Mono<TaskResponse> getTaskById(String taskId) {
        return taskRepository.findById(UUID.fromString(taskId))
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Cannot find Task with ID: " + taskId)))
            .map(taskMapper::fromTask);
    }

    @Override
    public Flux<TaskResponse> getAllTasks(String projectId) {
        return taskRepository.findByProjectId(projectId)
            .map(taskMapper::fromTask);
    }

    @Override
    @Transactional
    public Mono<Void> updateTask(String taskId, TaskRequest taskRequest) {
        return taskRepository.findById(UUID.fromString(taskId))
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Cannot find Task")))
            .flatMap(existingTask -> {
                if(StringUtils.hasText(taskRequest.userAssigned()) && !existingTask.getUserAssigned().equals(taskRequest.userAssigned())){
                    existingTask.setUserAssigned(taskId);
                }
                existingTask.setTitle(taskRequest.title());
                existingTask.setDescription(taskRequest.description());
                existingTask.setPriority(taskRequest.priority());
                existingTask.setStatus(taskRequest.status());
                existingTask.setFilesIncluded(taskRequest.filesIncluded().stream().collect(Collectors.toList()));
                return taskRepository.save(existingTask);
            })
            .doOnNext(taskUpdated -> {
                taskProducer.sendTaskUpdateEvent(
                    new TaskUpdate(
                        taskUpdated.getId().toString(),
                        taskUpdated.getTitle(),
                        taskUpdated.getStatus(),
                        taskUpdated.getUserAssigned(),
                        taskUpdated.getProjectId(),
                        taskUpdated.getCreatedAt(),
                        taskUpdated.getUpdatedAt()
                    )
                );
            })
            .then()
            .onErrorMap(e -> new TaskUpdateException("Cannot create Task"));
    }


    @Override
    @Transactional
    public Mono<Void> deleteTask(String taskId) {
        return taskRepository.findById(UUID.fromString(taskId))
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Cannot find task with id :" + taskId)))
            .flatMap(existingTask -> taskRepository.delete(existingTask))
            .then();
    }


    private boolean checkForUserExistance(String userId){
        return Boolean.valueOf(userClient.isUserExist(userId).getBody());
    }
    private boolean checkForProjectExistance(String projectId){
        return Boolean.valueOf(projectClient.isProjectExist(projectId).getBody());
    }
}
