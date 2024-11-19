package org.driventask.user.IService;

import org.driventask.user.Payload.Request.ChangePasswordRequest;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserResponse;

import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<Void> createUser(UserRequest userRequest);
    Mono<Void> updateUser(String id , UserRequest userRequest);
    Mono<UserResponse> getUser(String id);
    Mono<Void> deleteUser(String id);
    Mono<Void> changePassword(String id, ChangePasswordRequest changePasswordRequest);
    Mono<Boolean> isUserExist(String id);
}
