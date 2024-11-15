package org.driventask.task.Repository;

import java.util.UUID;

import org.driventask.task.Entity.Task;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import org.driventask.task.Enum.EStatus;
import org.driventask.task.Enum.EPriority;


@Repository
public interface TaskRepository extends ReactiveCrudRepository<Task, UUID>{
    Flux<Task> findByProjectId(String projectId);
    Flux<Task> findByStatus(EStatus status);
    Flux<Task> findByPriority(EPriority priority);
}
