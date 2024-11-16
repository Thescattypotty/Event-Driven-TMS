package org.driventask.project.Service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public class ProjectService {

    private final ProjectService projectService;

    private Mono<ResponseEntity<Void>> createProject(@RequestBody @Valid ProjectRequest projectRequest){

    }
}
