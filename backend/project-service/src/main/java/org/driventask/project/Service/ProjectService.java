package org.driventask.project.Service;

import java.util.List;
import java.util.UUID;

import org.driventask.project.Entity.Project;
import org.driventask.project.Exception.ProjectNotFoundException;
import org.driventask.project.IService.IProjectService;
import org.driventask.project.KafkaService.ProjectProducer;
import org.driventask.project.Payload.Kafka.ProjectCreation;
import org.driventask.project.Payload.Kafka.ProjectUpdate;
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
        return Mono.fromCallable(() -> {
            Project project = projectMapper.toProject(projectRequest);
            return project;
        })
        .flatMap(projectMapped -> projectRepository.save(projectMapped))
        .doOnNext(projectSaved->{
            projectProducer.sendProjectCreationEvent(
                new ProjectCreation(
                    projectSaved.getId().toString(),
                    projectSaved.getUserId().toString(),
                    projectSaved.getName(),
                    projectSaved.getDescription(),
                    projectSaved.getCreatedAt(),
                    projectSaved.getStartDate(),
                    projectSaved.getEndDate()
                )  
            );
        })
        .then()
        .onErrorMap(e -> new ProjectNotFoundException("Cannot create Project"));
    }


    @Override
    @Transactional
    public Mono<Void> updateProject(String projectId, ProjectRequest projectRequest) {
        return projectRepository.findById(UUID.fromString(projectId))
            .switchIfEmpty(Mono.error(new ProjectNotFoundException("Cannot find Project with ID :" + projectId)))
            .flatMap(project -> {
                Project projectUpdated = projectMapper.toProject(projectRequest);
                projectUpdated.setId(project.getId());
                return projectRepository.save(projectUpdated);
            })
            .doOnNext(projectSaved -> {
                projectProducer.sendProjectUpdateEvent(
                    new ProjectUpdate(
                        projectSaved.getId().toString(),
                        projectSaved.getUserId().toString(),
                        projectSaved.getName(),
                        projectSaved.getDescription(),
                        projectSaved.getUpdatedAt(),
                        projectSaved.getStartDate(),
                        projectSaved.getEndDate()
                    )
                );
            })
            .then()
            .onErrorMap(e -> new ProjectNotFoundException("Cannot create Project"));
    }

    @Override
    public Flux<ProjectResponse> getAllProjects(String userId) {
        return projectRepository.findByUserIdInFlux(userId)
            .switchIfEmpty(Mono.error(new ProjectNotFoundException("Cannot find Project with user ID :" + userId)))
            .map(projectMapper::fromProject);
    }

    @Override
    @Transactional
    public Mono<Void> deleteProject(String projectId) {
        return projectRepository.findById(UUID.fromString(projectId))
            .switchIfEmpty(Mono.error(new ProjectNotFoundException("Cannot find Project with ID:" + projectId)))
            .flatMap(existingProject -> projectRepository.delete(existingProject))
            .doOnTerminate(() -> System.out.println("Project with ID:" + projectId + " has been deleted"))
            .then();
    }


    @Override
    public Mono<ProjectResponse> getProjectById(String projectId) {
        return projectRepository.findById(UUID.fromString(projectId))
            .switchIfEmpty(Mono.error(new ProjectNotFoundException("Cannot find Project with ID:" + projectId)))
            .map(projectMapper::fromProject);
    }

}
