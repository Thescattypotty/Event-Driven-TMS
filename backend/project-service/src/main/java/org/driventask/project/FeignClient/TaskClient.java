package org.driventask.project.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import reactor.core.publisher.Mono;

@FeignClient(name="task-service",url="${application.config.task-url}")
public interface TaskClient {

    @GetMapping("/verify/{taskId}")
    public Mono<ResponseEntity<Boolean>> verifyTaskExistance(@PathVariable String taskId);
    
} 