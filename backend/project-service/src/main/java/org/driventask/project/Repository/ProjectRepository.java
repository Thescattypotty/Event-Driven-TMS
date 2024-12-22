package org.driventask.project.Repository;

import java.util.UUID;

import org.driventask.project.Entity.Project;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;


@Repository
public interface ProjectRepository extends R2dbcRepository<Project,UUID>{
    Flux<Project> findByUserId(String userId);
}
