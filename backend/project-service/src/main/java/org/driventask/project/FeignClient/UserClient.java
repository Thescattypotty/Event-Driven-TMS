package org.driventask.project.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import reactor.core.publisher.Mono;

@FeignClient(name="user-service",url="${application.config.user-url}")
public interface UserClient {
    
    @GetMapping("/exists/{id}")
    Mono<ResponseEntity<Boolean>> isUserExist(@PathVariable String id);
}
