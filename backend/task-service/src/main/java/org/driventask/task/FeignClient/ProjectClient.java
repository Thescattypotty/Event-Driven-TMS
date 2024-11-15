package org.driventask.task.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import reactor.core.publisher.Mono;

@FeignClient(name = "project-service", url = "${application.config.project-url}")
public interface ProjectClient {

    @GetMapping("/{id}")
    Mono<ResponseEntity<Boolean>> isProjectExist(@PathVariable String id);
}
