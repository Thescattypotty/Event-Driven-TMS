package org.driventask.project.Service;

import lombok.RequiredArgsConstructor;

import org.driventask.project.Entity.Project;
import org.driventask.project.IService.IProjectService;
import org.driventask.project.KafkaService.ProjectProducer;
import org.driventask.project.Payload.Kafka.ProjectCreation;
import org.driventask.project.Payload.Mapper.ProjectMapper;
import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import org.driventask.project.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    @Autowired
    private final ProjectProducer projectProducer;

    @Override
    @Transactional
    public Mono<Void> createProject(ProjectRequest projectRequest) {
        Project project = projectMapper.toProject(projectRequest);
        return projectRepository.save(project)
                .flatMap(projectCreated -> {
                    projectProducer.sendProjectCreationEvent(
                            new ProjectCreation(
                                    projectCreated.getId().toString(),
                                    projectCreated.getUserId().toString(),
                                    projectCreated.getName(),
                                    projectCreated.getDescription(),
                                    projectCreated.getCreatedAt(),
                                    projectCreated.getStartDate(),
                                    projectCreated.getEndDate()
                            )
                    );
                    return Mono.empty();
                });
    }

    

    @Override
    public Flux<ProjectResponse> getAllProjects(String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProjects'");
    }

    

    @Override
    public Mono<Void> deleteProject(String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteProject'");
    }



    @Override
    public Mono<ProjectResponse> getProjectById(String taskId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectById'");
    }



    @Override
    public Mono<Void> updateProject(String projectId, ProjectRequest projectRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProject'");
    }

    


}
