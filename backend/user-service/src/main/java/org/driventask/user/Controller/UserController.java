package org.driventask.user.Controller;

import java.util.List;

import org.driventask.user.Payload.Request.UserAuthRequest;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserAuthResponse;
import org.driventask.user.Payload.Response.UserResponse;
import org.driventask.user.Service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable String id){
        return userService.getUser(id)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("getUser executed"))
            .doOnError(e -> log.error("Error durring get User"))
            .map(user -> new ResponseEntity<>(user,HttpStatus.OK))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal));
    }
    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<UserResponse>> getUserByEmail(@PathVariable String email){
        return userService.getUserByEmail(email)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("getUserByEmail executed"))
            .doOnError(e -> log.error("Error durring get User by Email"))
            .map(user -> new ResponseEntity<>(user,HttpStatus.OK))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal));
    }
    
    @GetMapping("/exists/{id}")
    public Mono<ResponseEntity<Boolean>> isUserExist(@PathVariable String id) {
        return userService.isUserExist(id)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("isUserExist executed"))
            .doOnError(e -> log.error("Error durring verifying User Existance"))
            .map(isExisying -> new ResponseEntity<>(isExisying, HttpStatus.ACCEPTED))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal));
    }
    
    @PostMapping
    public Mono<ResponseEntity<Void>> saveUser(@RequestBody @Valid UserRequest userRequest){
        return userService.createUser(userRequest)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("saveUser executed"))
            .doOnError(e -> log.error("Error durring user Creation"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updtadeUser(@PathVariable String id , @RequestBody @Valid UserRequest userRequest){
        return userService.updateUser(id,userRequest)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("updtadeUser executed"))
            .doOnError(e -> log.error("Error durring updtade User"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED)));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id){
        return userService.deleteUser(id)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("deleteUser executed"))
            .doOnError(e -> log.error("Error durring delete User"))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal))
            .then(Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED)));
    }

    @PostMapping("/auth/verify")
    public Mono<ResponseEntity<UserAuthResponse>> verifyUserCredentials(@RequestBody UserAuthRequest userAuthRequest){
        log.info("Hello world");
        return userService.verifyUserCredentials(userAuthRequest)
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("verifyUserCredentials executed"))
            .doOnError(e -> log.error("Error durring verifyUserCredentials"))
            .map(userResponse -> new ResponseEntity<>(userResponse, HttpStatus.OK))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal));
    }

    @GetMapping
    public Mono<ResponseEntity<List<UserResponse>>> getAllUsers(){
        return userService.getAllUsers()
            .doOnSubscribe(sub -> log.info("Subscription Started"))
            .doOnNext(next -> log.info("getAllUsers executed"))
            .doOnError(e -> log.error("Error durring get All Users"))
            .collectList()
            .map(userResponse -> new ResponseEntity<>(userResponse, HttpStatus.OK))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal));
    }
}
