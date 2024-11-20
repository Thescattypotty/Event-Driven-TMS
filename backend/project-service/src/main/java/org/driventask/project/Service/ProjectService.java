package org.driventask.project.Service;

import lombok.RequiredArgsConstructor;

import org.driventask.project.FeignClient.TaskClient;
import org.driventask.project.IService.IProjectService;
import org.driventask.project.Payload.Mapper.ProjectMapper;
import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import org.driventask.project.Repository.ProjectRepository;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService{

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final TaskClient taskClient;


    @Override
    public Mono<Void> createProject(ProjectRequest projectRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'createProject'");
    }

    @Override
    public Mono<ProjectResponse> getProjectById(String taskId) {
        
        throw new UnsupportedOperationException("Unimplemented method 'getProjectById'");
    }

    @Override
    public Flux<ProjectResponse> getAllProjects(String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProjects'");
    }

    @Override
    public Mono<Void> updateProject(String projectId, ProjectRequest projectRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProject'");
    }

    @Override
    public Mono<Void> deleteProject(String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteProject'");
    }

    
}

