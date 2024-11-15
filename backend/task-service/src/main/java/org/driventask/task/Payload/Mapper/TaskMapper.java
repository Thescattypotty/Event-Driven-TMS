package org.driventask.task.Payload.Mapper;

import java.util.Optional;

import org.driventask.task.Entity.Task;
import org.driventask.task.Enum.EPriority;
import org.driventask.task.Enum.EStatus;
import org.driventask.task.Payload.Request.TaskRequest;
import org.driventask.task.Payload.Response.TaskResponse;
import org.springframework.stereotype.Service;

@Service
public class TaskMapper {
    
    public Task toTask(TaskRequest taskRequest){
        return Task.builder()
            .title(taskRequest.title())
            .description(taskRequest.description())
            .priority(Optional.ofNullable(taskRequest.priority()).orElse(EPriority.LOW))
            .status(Optional.ofNullable(taskRequest.status()).orElse(EStatus.TO_DO))
            .userAssigned(taskRequest.userAssigned())
            .projectId(taskRequest.projectId())
            .filesIncluded(taskRequest.filesIncluded())
            .build();
    }

    public TaskResponse fromTask(Task task){
        return new TaskResponse(
            task.getId().toString(),
            task.getTitle(),
            task.getDescription(),
            task.getDueDate(),
            task.getPriority(),
            task.getStatus(),
            task.getHistoryOfTask(),
            task.getFilesIncluded(),
            task.getUserAssigned(),
            task.getProjectId(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
