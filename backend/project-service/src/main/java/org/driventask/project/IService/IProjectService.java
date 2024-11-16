package org.driventask.project.IService;

import org.driventask.project.Payload.Request.ProjectRequest;
import reactor.core.publisher.Mono;

public interface IProjectService {
    Mono<Void> createProject(ProjectRequest projectRequest);
    Mono<ProjectResponse> getProjectById(String taskId);
    Flux<ProjectResponse> getAllProjects(String projectId);
    Mono<Void> updateProject(String projectId , ProjectRequest projectRequest);
    Mono<Void> deleteProject(String projectId);
}
