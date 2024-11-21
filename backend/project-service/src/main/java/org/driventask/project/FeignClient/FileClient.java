package org.driventask.project.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="file-service",url="${application.config.file-url}")
public interface FileClient {

    
} 