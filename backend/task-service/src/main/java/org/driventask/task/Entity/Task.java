package org.driventask.task.Entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.driventask.task.Enum.EPriority;
import org.driventask.task.Enum.EStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tasks")
public class Task {
    
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String title;

    private String description;

    private LocalDateTime dueDate;

    private EPriority priority;

    private EStatus status;

    private String projectId;

    @Column("history_of_task")
    private List<String> historyOfTask;

    @Column("files_included")
    private List<String> filesIncluded;

    private String userAssigned;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
