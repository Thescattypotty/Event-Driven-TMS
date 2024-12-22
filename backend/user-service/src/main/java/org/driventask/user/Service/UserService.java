package org.driventask.user.Service;

import java.util.UUID;

import org.driventask.user.Entity.User;
import org.driventask.user.Enum.EUserEvent;
import org.driventask.user.Exception.PasswordIncorrectException;
import org.driventask.user.Exception.UserFailedCreationException;
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
import reactor.core.publisher.Flux;
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
        return Mono.fromCallable(() -> {
            User user = userMapper.toUser(userRequest);
            String encodedPassword = passwordEncoder.encode(userRequest.password());
            user.setPassword(encodedPassword);
            return user;
            })
            .flatMap(user -> userRepository.save(user)
                .flatMap(userCreated -> {
                    return Mono.fromRunnable(() -> {
                        userProducer.handleUserCreation(
                            new UserCreation(
                                userCreated.getId().toString(),
                                userCreated.getFullName(),
                                userCreated.getEmail(),
                                userCreated.getCreatedAt()
                                )
                            );
                        }
                    );
                })
            ).then()
            .onErrorMap(e -> new UserFailedCreationException("Cannot create user for now")
        );
    }

    @Override
    @Transactional
    public Mono<Void> updateUser(String id, UserRequest userRequest) {
        return userRepository.findById(UUID.fromString(id))
            .switchIfEmpty(Mono.error(new UserNotFoundException("Cannot find User")))
            .flatMap(userFounded -> {
                userFounded.setFullName(userRequest.fullname());
                userFounded.setEmail(userRequest.email());
                userFounded.setRoles(userRequest.roles());
                return userRepository.save(userFounded);
            })
            .doOnNext(userSaved -> {
                userProducer.handleUserUpdate(
                    new UserUpdated(
                    userSaved.getId().toString(),
                    userSaved.getFullName(),
                    userSaved.getEmail(),
                    EUserEvent.USER_CHANGE_INFORMATIONS,
                    userSaved.getUpdatedAt()
                    )
                );
            })
            .then()
            .onErrorMap(e -> new UserFailedCreationException("Cannot update user for now"));
    }

    @Override
    public Mono<UserResponse> getUser(String id) {
        return userRepository.findById(UUID.fromString(id))
            .onErrorMap(e -> new UserNotFoundException("Cannot find user with ID :" + id))
            .map(userMapper::fromUser);
    }

    @Override
    public Mono<UserResponse> getUserByEmail(String email){
        return userRepository.findByEmail(email)
            .onErrorMap(e -> new UserNotFoundException("Cannot find user with email :" + email))
            .map(userMapper::fromUser);
    }

    @Override
    @Transactional
    public Mono<Void> deleteUser(String id) {
        return userRepository.findById(UUID.fromString(id))
            .switchIfEmpty(Mono.error(new UserNotFoundException("Cannot find User")))
            .flatMap(user  -> userRepository.deleteById(UUID.fromString(id)))
            .then();  
    }

    @Override
    @Transactional
    public Mono<Void> changePassword(String id, ChangePasswordRequest changePasswordRequest) {
        return userRepository.findById(UUID.fromString(id))
            .switchIfEmpty(Mono.error(new UserNotFoundException("Cannot find User")))
            .flatMap(userFounded -> {
                if(passwordEncoder.matches(changePasswordRequest.oldPassword(), userFounded.getPassword())){
                    userFounded.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
                    return userRepository.save(userFounded);
                }else{
                    return Mono.error(new PasswordIncorrectException("Password is Incorrect"));
                }
            }
        ).doOnNext(userSaved -> {
            userProducer.handleUserUpdate(
                new UserUpdated(
                    userSaved.getId().toString(),
                    userSaved.getFullName(),
                    userSaved.getEmail(),
                    EUserEvent.USER_CHANGE_PASSWORD,
                    userSaved.getUpdatedAt()
                    )
                );
            }
        )
        .then()
        .onErrorMap(e -> new PasswordIncorrectException("Password is Incorrect"));
    }

    @Override
    public Mono<Boolean> isUserExist(String id) {
        return userRepository.existsById(UUID.fromString(id));
    }

    @Override
    @Transactional
    public Mono<UserAuthResponse> verifyUserCredentials(UserAuthRequest userAuthRequest) {
        return userRepository.findByEmail(userAuthRequest.email())
            .switchIfEmpty(Mono.error(new UserNotFoundException("User does not exist")))
            .flatMap(user -> {
                if (passwordEncoder.matches(userAuthRequest.password(), user.getPassword())) {
                    return Mono.just(new UserAuthResponse(user.getEmail(), user.getRoles()));
                } else {
                    return Mono.error(new PasswordIncorrectException("Password is incorrect"));
                }
            }
        );
    }

    @Override
    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
            .map(userMapper::fromUser);
    }

    
}

