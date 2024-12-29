package org.driventask.project.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;

import java.util.List;

import org.driventask.project.Payload.Request.ProjectRequest;
import org.driventask.project.Payload.Response.ProjectResponse;
import org.driventask.project.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/project")
public class ProjectController {
    
    private final ProjectService projectService;

    @PostMapping
    public Mono<ResponseEntity<Void>> createProject(@RequestBody @Valid ProjectRequest projectRequest){
        return projectService.createProject(projectRequest)
            .doOnSubscribe(sub -> log.info("Subscription createProject Started"))
            .doOnNext(next -> log.info("createProject executed"))
            .doOnError(e -> log.error("Error during Project Creation", e))
            .doFinally(signal -> log.info("Processing completed with signal: {}", signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
            
    }

    @GetMapping("/{projectId}")
    public Mono<ResponseEntity<ProjectResponse>> getProjectById(@PathVariable("projectId") String projectId) {
        return projectService.getProjectById(projectId)
            .map(projectResponse -> new ResponseEntity<>(projectResponse, HttpStatus.OK));
    }

    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<List<ProjectResponse>>> getAllProjects(@PathVariable("userId") String userId) {
        return projectService.getAllProjects(userId)
            .doOnSubscribe(sub -> log.info("Subscription getAllProjects Started"))
            .doOnNext(next -> log.info("getAllProjects executed"))
            .doOnError(e -> log.info("Error durring Project Creation"))
            .collectList()
            .map(projectResponse -> new ResponseEntity<>(projectResponse, HttpStatus.OK))
            .doFinally(signal -> log.error("Processing completed with signal: " + signal));
    }

    @PutMapping("/{projectId}")
    public Mono<ResponseEntity<Void>> updateProject(@PathVariable("projectId") String projectId, ProjectRequest projectRequest) {
        return projectService.updateProject(projectId,projectRequest)
            .doOnSubscribe(sub -> log.info("Subscription updateProject Started"))
            .doOnNext(next -> log.info("updateProject executed"))
            .doOnError(e -> log.error("Error during Project Update", e))
            .doFinally(signal -> log.info("Processing completed with signal: {}", signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.OK)));        
    }

    @DeleteMapping("/{projectId}")
    public Mono<ResponseEntity<Void>> deleteProject(@PathVariable("projectId") String projectId){
        return projectService.deleteProject(projectId)
            .doOnSubscribe(sub -> log.info("Subscription deleteProject Started"))
            .doOnNext(next -> log.info("deleteProject executed"))
            .doOnError(e -> log.error("Error during Project Deletion", e))
            .doFinally(signal -> log.info("Processing completed with signal: {}", signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }
}
