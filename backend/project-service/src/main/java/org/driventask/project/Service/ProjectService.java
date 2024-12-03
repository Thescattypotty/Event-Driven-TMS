package org.driventask.project.Service;

import java.util.Set;
import java.util.UUID;

import org.driventask.project.Entity.Project;
import org.driventask.project.Exception.ProjectNotFoundException;
import org.driventask.project.IService.IProjectService;
import org.driventask.project.KafkaService.ProjectProducer;
import org.driventask.project.Payload.Kafka.ProjectCreation;
import org.driventask.project.Payload.Mapper.ProjectMapper;
import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import org.driventask.project.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
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
                }
            );
    }


    @Override
    @Transactional
    public Mono<Void> updateProject(String projectId, ProjectRequest projectRequest) {
        return projectRepository.findById(UUID.fromString(projectId))
            .flatMap(project -> {
                Project updatedProject = projectMapper.toProject(projectRequest);
                updatedProject.setId(project.getId());
                projectRepository.save(updatedProject);
                return Mono.empty();
            }
        );
    }

    

    @Override
    public Flux<ProjectResponse> getAllProjects(String userId) {
        return projectRepository.findByUserId(Set.of(userId))
            .map(projectMapper::fromProject);
    }

    

    @Override
    @Transactional
    public Mono<Void> deleteProject(String projectId) {
        return projectRepository.findById(UUID.fromString(projectId))
            .flatMap(existingProject -> projectRepository.delete(existingProject))
            .switchIfEmpty(Mono.error(new ProjectNotFoundException("Cannot find Project with ID:" + projectId)))
            .doOnTerminate(null)
            .then();
    }



    @Override
    public Mono<ProjectResponse> getProjectById(String projectId) {
        return projectRepository.findById(UUID.fromString(projectId))
            .map(projectMapper::fromProject)
            .switchIfEmpty(Mono.error(new ProjectNotFoundException("Cannot find Project with ID:" + projectId)));
    }

}
