package org.driventask.gateway.Configuration;

import org.driventask.gateway.Component.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableHystrix
@RequiredArgsConstructor
public class GatewayConfiguration {
    
    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder routeLocatorBuilder){
        return routeLocatorBuilder.routes()
            .route(
                "user-service", r -> r.path("/api/v1/user/**")
                .filters(f -> f.filter(filter))
                .uri("lb://user-service")
            )
            .route(
                "auth-service", r -> r.path("/api/v1/auth/**")
                .filters(f -> f.filter(filter))
                .uri("lb://auth-service")
            )
            .route(
                "task-service", r -> r.path("/api/v1/task/**")
                .filters(f -> f.filter(filter))
                .uri("lb://task-service")
            )
            .route(
                "project-service", r -> r.path("/api/v1/project/**")
                .filters(f -> f.filter(filter))
                .uri("lb://project-service")
            )
            .route(
                "file-service", r -> r.path("/api/v1/file/**")
                .filters(f -> f.filter(filter))
                .uri("lb://file-service")
            )
            .build();
    }
}
