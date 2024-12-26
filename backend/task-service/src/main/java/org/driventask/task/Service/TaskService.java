package org.driventask.task.Service;

import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.driventask.task.Exception.ProjectNotFoundException;
import org.driventask.task.Exception.TaskNotFoundException;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
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
        /* 
        return projectClient.isProjectExist(taskRequest.projectId())
            .flatMap(projectResponse -> {
                if (Boolean.FALSE.equals(projectResponse.getBody())) {
                    return Mono.error(new ProjectNotFoundException("Project not found with ID: " + taskRequest.projectId()));
                }
                if(StringUtils.hasText(taskRequest.userAssigned())){
                    return userClient.isUserExist(taskRequest.userAssigned())
                        .flatMap(userResponse -> {
                            if(Boolean.FALSE.equals(userResponse.getBody())){
                                return Mono.error(new UserNotFoundException("User not found with ID: " + taskRequest.userAssigned()));
                            }
                            return saveTaskAndPublishEvents(taskRequest);
                        });
                }
                return saveTaskAndPublishEvents(taskRequest);
            }
        );
        */
        return Mono.fromCallable(() -> {
            System.out.println("Hello world");
            return taskRequest;
        })
        .flatMap(task -> taskRepository.save(taskMapper.toTask(taskRequest))
                .flatMap(taskCreated -> {
                    return Mono.fromRunnable(() -> {
                        taskProducer.sendTaskCreationEvent(
                            new TaskCreation(
                                taskCreated.getId().toString(),
                                taskCreated.getTitle(),
                                taskCreated.getProjectId(),
                                taskCreated.getUserAssigned(),
                                taskCreated.getCreatedAt()
                            )
                        );
                        System.out.println("Hello Task");
                    });
                }
            ).then()
            .onErrorMap(e -> new RuntimeException("Cannot create Task"))
        );
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
                if (StringUtils.hasText(taskRequest.userAssigned())) {
                    return userClient.isUserExist(taskRequest.userAssigned())
                        .flatMap(userResponse -> {
                            if (Boolean.FALSE.equals(userResponse.getBody())) {
                                return Mono.error(new UserNotFoundException("User not found with ID: " + taskRequest.userAssigned()));
                            }
                            existingTask.setTitle(taskRequest.title());
                            existingTask.setDescription(taskRequest.description());
                            existingTask.setPriority(taskRequest.priority());
                            existingTask.setStatus(taskRequest.status());
                            existingTask.setUserAssigned(taskRequest.userAssigned());
                            existingTask.setFilesIncluded(taskRequest.filesIncluded().stream().collect(Collectors.toList()));

                            return taskRepository.save(existingTask);
                        });
                } else {
                    existingTask.setTitle(taskRequest.title());
                    existingTask.setDescription(taskRequest.description());
                    existingTask.setPriority(taskRequest.priority());
                    existingTask.setStatus(taskRequest.status());
                    existingTask.setUserAssigned(taskRequest.userAssigned());
                    existingTask.setFilesIncluded(taskRequest.filesIncluded().stream().collect(Collectors.toList()));

                    return taskRepository.save(existingTask);
                }
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
            }
        )
        .then()
        .onErrorMap(e -> new TaskNotFoundException("Cannot create Task"));
    }


    @Override
    @Transactional
    public Mono<Void> deleteTask(String taskId) {
        return taskRepository.findById(UUID.fromString(taskId))
            .switchIfEmpty(Mono.error(new TaskNotFoundException("Cannot find task with id :" + taskId)))
            .flatMap(existingTask -> taskRepository.delete(existingTask))
            .then();
    }

    private Mono<Void> saveTaskAndPublishEvents(TaskRequest taskRequest) {
        return taskRepository.save(taskMapper.toTask(taskRequest))
            .doOnNext(savedTask -> {
                taskProducer.sendTaskCreationEvent(
                    new TaskCreation(
                        savedTask.getId().toString(),
                        savedTask.getTitle(),
                        savedTask.getProjectId(),
                        savedTask.getUserAssigned(),
                        savedTask.getCreatedAt()
                        )
                    );
                }
            )
            .then();
    }
}
