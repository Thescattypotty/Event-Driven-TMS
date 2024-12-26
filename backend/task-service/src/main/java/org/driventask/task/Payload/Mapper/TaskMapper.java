package org.driventask.task.Payload.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.driventask.task.Entity.Task;
import org.driventask.task.Enum.EPriority;
import org.driventask.task.Enum.EStatus;
import org.driventask.task.Payload.Request.TaskRequest;
import org.driventask.task.Payload.Response.TaskResponse;
import org.springframework.stereotype.Service;

@Service
public class TaskMapper {
    
    public Task toTask(TaskRequest taskRequest){
        if (taskRequest == null) {
            return null;
        }
        
        return Task.builder()
            .title(Optional.ofNullable(taskRequest.title()).orElse(""))
            .description(Optional.ofNullable(taskRequest.description()).orElse(""))
            .priority(Optional.ofNullable(taskRequest.priority()).orElse(EPriority.LOW))
            .status(Optional.ofNullable(taskRequest.status()).orElse(EStatus.TO_DO))
            .userAssigned(Optional.ofNullable(taskRequest.userAssigned()).orElse(""))
            .projectId(Optional.ofNullable(taskRequest.projectId()).orElse(""))
            .filesIncluded(
                taskRequest.filesIncluded().isEmpty() ? 
                    Collections.emptyList() : 
                    taskRequest.filesIncluded().stream().collect(Collectors.toList())
            )
            .build();
    }

    public TaskResponse fromTask(Task task){
        if (task == null) {
            return null;
        }
        
        return new TaskResponse(
            Optional.ofNullable(task.getId()).map(Object::toString).orElse(""),
            Optional.ofNullable(task.getTitle()).orElse(""),
            Optional.ofNullable(task.getDescription()).orElse(""),
            task.getDueDate(),
            Optional.ofNullable(task.getPriority()).orElse(EPriority.LOW),
            Optional.ofNullable(task.getStatus()).orElse(EStatus.TO_DO),
            task.getHistoryOfTask().isEmpty() ? Collections.emptySet() : task.getHistoryOfTask().stream().collect(Collectors.toSet()),
            task.getFilesIncluded().isEmpty() ? Collections.emptySet() : task.getFilesIncluded().stream().collect(Collectors.toSet()),
            Optional.ofNullable(task.getUserAssigned()).orElse(""),
            Optional.ofNullable(task.getProjectId()).orElse(""),
            Optional.ofNullable(task.getCreatedAt()).orElse(null),
            Optional.ofNullable(task.getUpdatedAt()).orElse(null)
        );
    }
}
