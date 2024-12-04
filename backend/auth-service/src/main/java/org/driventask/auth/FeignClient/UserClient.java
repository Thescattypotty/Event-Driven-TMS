package org.driventask.auth.FeignClient;

import org.driventask.auth.Payload.Request.UserAuthRequest;
import org.driventask.auth.Payload.Response.UserAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "user-service",
    url = "${application.config.user-url}"
)
public interface UserClient {
    @PostMapping("/auth/verify")
    UserAuthResponse verifyUserCredentials(@RequestBody UserAuthRequest userAuthRequest); 
}
