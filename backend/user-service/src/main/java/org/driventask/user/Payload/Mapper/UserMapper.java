package org.driventask.user.Payload.Mapper;

import org.driventask.user.Entity.User;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(UserRequest userRequest){
        return User
            .builder()
            .fullName(userRequest.fullname())
            .email(userRequest.email())
            .password(userRequest.password())
            .roles(userRequest.roles())
            .build();
    }
    public UserResponse fromUser(User user){
        return new UserResponse(user.getId().toString(), user.getFullName(), user.getEmail());
    }
}
