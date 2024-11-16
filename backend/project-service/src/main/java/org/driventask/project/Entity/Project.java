package org.driventask.project.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "projects")
public class Project {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String name;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String userId;

    private String taskId;

    private String fileId;

}
