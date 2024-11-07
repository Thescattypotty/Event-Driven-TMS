package org.driventask.user.IService;

import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserResponse;

public interface IUserService {
    void createUser(UserRequest userRequest);
    void updateUser(String id , UserRequest userRequest);
    UserResponse getUser(String id);
    void deleteUser(String id);
}
