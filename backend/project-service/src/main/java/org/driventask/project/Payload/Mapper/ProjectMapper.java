package org.driventask.project.Payload.Mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import org.driventask.project.Entity.Project;
import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import org.springframework.stereotype.Service;

@Service
public class ProjectMapper {

    public Project toProject(ProjectRequest projectRequest) {
        if (projectRequest == null) {
            throw new IllegalArgumentException("ProjectRequest cannot be null");
        }

        return Project.builder()
                .name(projectRequest.name() != null ? projectRequest.name() : "")
                .description(projectRequest.description() != null ? projectRequest.description() : "")
                .startDate(projectRequest.startDate())
                .endDate(projectRequest.endDate())
                .userId(projectRequest.userId() != null ? projectRequest.userId().stream().collect(Collectors.toList()) : Collections.emptyList())
                .fileId(projectRequest.file_id() != null ? projectRequest.file_id().stream().collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public ProjectResponse fromProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }

        return new ProjectResponse(
                project.getId() != null ? project.getId().toString() : "",
                project.getName() != null ? project.getName() : "",
                project.getDescription() != null ? project.getDescription() : "",
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                project.getUserId() != null ? project.getUserId().stream().collect(Collectors.toSet()) : Collections.emptySet(),
                project.getTaskId() != null ? project.getTaskId().stream().collect(Collectors.toSet()) : Collections.emptySet(),
                project.getFileId() != null ? project.getFileId().stream().collect(Collectors.toSet()) : Collections.emptySet()
        );
    }
}
