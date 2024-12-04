package org.driventask.user.Service;

import java.util.UUID;

import org.driventask.user.Entity.User;
import org.driventask.user.Enum.EUserEvent;
import org.driventask.user.Exception.PasswordIncorrectException;
import org.driventask.user.Exception.UserNotFoundException;
import org.driventask.user.IService.IUserService;
import org.driventask.user.Payload.Kafka.UserCreation;
import org.driventask.user.Payload.Kafka.UserUpdated;
import org.driventask.user.Payload.Mapper.UserMapper;
import org.driventask.user.Payload.Request.ChangePasswordRequest;
import org.driventask.user.Payload.Request.UserAuthRequest;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserAuthResponse;
import org.driventask.user.Payload.Response.UserResponse;
import org.driventask.user.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Mono<Void> createUser(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        String encodedPassword = passwordEncoder.encode(userRequest.password());
        user.setPassword(encodedPassword);
        System.out.println("user");
        return userRepository.save(user)
            .doOnNext(userCreated -> {
            System.out.println("Saved user: " + userCreated.getId());
            userProducer.handleUserCreation(
                new UserCreation(
                    userCreated.getId().toString(),
                    userCreated.getFullName(),
                    userCreated.getEmail(),
                    userCreated.getCreatedAt()
                    )
                );
            }
        ).then();
    }

    @Override
    @Transactional
    public Mono<Void> updateUser(String id, UserRequest userRequest) {
        return userRepository.findById(UUID.fromString(id))
            .flatMap(userFounded -> {
                userFounded.setFullName(userRequest.fullname());
                userFounded.setEmail(userRequest.email());
                userFounded.setRoles(userRequest.roles());

                return userRepository.save(userFounded)
                    .flatMap(userUpdated -> {
                        userProducer.handleUserUpdate(
                            new UserUpdated(
                                userUpdated.getId().toString(),
                                userUpdated.getFullName(),
                                userUpdated.getEmail(),
                                EUserEvent.USER_CHANGE_INFORMATIONS,
                                userUpdated.getUpdatedAt()
                            )
                        );
                        return Mono.empty();
                    }
                );
            }
        );
    }


    @Override
    public Mono<UserResponse> getUser(String id) {
        return userRepository.findById(UUID.fromString(id))
            .map(userMapper::fromUser);
    }

    @Override
    public Mono<Void> deleteUser(String id) {

        return userRepository.findById(UUID.fromString(id))
            .flatMap(user  -> userRepository.deleteById(UUID.fromString(id)))
            .switchIfEmpty(Mono.error(new UserNotFoundException("Cannot find user with ID :" + id)))
            .then();
            
    }

    @Override
    public Mono<Void> changePassword(String id, ChangePasswordRequest changePasswordRequest) {

        return userRepository.findById(UUID.fromString(id))
            .flatMap(userFound-> {
                if(passwordEncoder.matches(changePasswordRequest.oldPassword(), userFound.getPassword())){
                    userFound.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
                    return userRepository.save(userFound)
                        .flatMap(userChanged -> {
                            userProducer.handleUserUpdate(
                                new UserUpdated(
                                    userChanged.getId().toString(),
                                    userChanged.getFullName(),
                                    userChanged.getEmail(),
                                    EUserEvent.USER_CHANGE_PASSWORD,
                                    userChanged.getUpdatedAt()
                                )
                            );
                            return Mono.empty();
                        }
                    );
                }
                return Mono.error(new PasswordIncorrectException("Password is incorrect"));
            }
        );
    }

    @Override
    public Mono<Boolean> isUserExist(String id) {
        return userRepository.existsById(UUID.fromString(id));
    }

    @Override
    public Mono<UserAuthResponse> verifyUserCredentials(UserAuthRequest userAuthRequest) {
        return userRepository.findByEmail(userAuthRequest.email())
            .switchIfEmpty(Mono.error(new UserNotFoundException("User does not exist")))
            .flatMap(user -> {
                if (passwordEncoder.matches(userAuthRequest.password(), user.getPassword())) {
                    return Mono.just(new UserAuthResponse(user.getEmail(), user.getRoles()));
                } else {
                    return Mono.error(new PasswordIncorrectException("Password is incorrect"));
                }
            });
    }

    
}

