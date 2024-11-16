package org.driventask.project.Payload.Mapper;

import org.driventask.project.Entity.Project;
import org.springframework.stereotype.Service;
import org.driventask.project.Payload.Request.ProjectRequest;

@Service
public class ProjectMapper {

        public Project toProject(ProjectRequest projectRequest){
            return Project.builder()
                    .name(projectRequest.name())
                    .description(projectRequest.description())
                    .startDate(projectRequest.startDate())
                    .endDate(projectRequest.endDate())
                    .createdAt(projectRequest.createdAt())
                    .updatedAt(projectRequest.updatedAt())
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
                    project.getUpdatedAt()
            );

}
