package org.driventask.task.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", url = "${application.config.project-url}")
public interface ProjectClient {

    @GetMapping("/{id}")
    ResponseEntity<String> isProjectExist(@PathVariable String id);
}
