package org.driventask.project.IService;

import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface IProjectService {
    Mono<Void> createProject(ProjectRequest projectRequest);
    Mono<ProjectResponse> getProjectById(String taskId);
    Flux<ProjectResponse> getAllProjects(String projectId);
    Mono<Void> updateProject(String projectId , ProjectRequest projectRequest);
    Mono<Void> deleteProject(String projectId);
}
