package org.driventask.auth.FeignClient;

import org.driventask.auth.Payload.Request.UserAuthRequest;
import org.driventask.auth.Payload.Response.UserAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import reactor.core.publisher.Mono;

@FeignClient(
    name = "user-service",
    url = "${application.config.user-url}"
)
public interface UserClient {

    @GetMapping("/auth/verify")
    Mono<UserAuthResponse> verifyUserCredentials(@RequestBody UserAuthRequest userAuthRequest); 

}
