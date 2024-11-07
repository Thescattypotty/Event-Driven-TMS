package org.driventask.user.Service;

import java.util.UUID;

import org.driventask.user.Entity.User;
import org.driventask.user.Exception.UserNotFoundException;
import org.driventask.user.IService.IUserService;
import org.driventask.user.Payload.Mapper.UserMapper;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserResponse;
import org.driventask.user.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        String encodedPassword = passwordEncoder.encode(userRequest.password());
        user.setPassword(encodedPassword);
        userRepository.save(user);  
    }

    @Override
    public void updateUser(String id, UserRequest userRequest) {
        User user = userRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new UserNotFoundException("Cannot find user with id :" + id));
        user.setFullName(userRequest.fullName());
        user.setEmail(userRequest.email());
        user.setRoles(userRequest.roles());
        userRepository.save(user);
    }

    @Override
    public UserResponse getUser(String id) {
        User user = userRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new UserNotFoundException("Cannot find user with id :" + id));
        return userMapper.fromUser(user);
    }

    @Override
    public void deleteUser(String id) {
        if(userRepository.existsById(UUID.fromString(id))){
            userRepository.deleteById(UUID.fromString(id));
        }
    }
    
}

