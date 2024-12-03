package org.driventask.project.Payload.Mapper;

import org.driventask.project.Entity.Project;
import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import org.springframework.stereotype.Service;

@Service
public class ProjectMapper {

        public Project toProject(ProjectRequest projectRequest){
            return Project.builder()
                    .name(projectRequest.name())
                    .description(projectRequest.description())
                    .startDate(projectRequest.startDate())
                    .endDate(projectRequest.endDate())
                    // we need to add users & files id , by verifying them
                    .build();
        }

        public ProjectResponse fromProject(Project project){
            return new ProjectResponse(
                    project.getId().toString(),
                    project.getName(),
                    project.getDescription(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getCreatedAt(),
                    project.getUpdatedAt(),
                    project.getUserId(),
                    project.getTaskId(),
                    project.getFileId()
            );

        }
}
