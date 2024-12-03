package org.driventask.user.Controller;

import org.driventask.user.Payload.Request.UserAuthRequest;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserAuthResponse;
import org.driventask.user.Payload.Response.UserResponse;
import org.driventask.user.Service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class UserController {
    
    private final UserService userService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable String id){
        return userService.getUser(id)
            .map(user -> new ResponseEntity<>(user,HttpStatus.OK));
    }
    
    @GetMapping("/exists/{id}")
    public Mono<ResponseEntity<Boolean>> isUserExist(@PathVariable String id) {
        return userService.isUserExist(id)
            .map(isExisying -> new ResponseEntity<>(isExisying, HttpStatus.ACCEPTED));
    }
    
    @PostMapping("/create")
    public Mono<ResponseEntity<Void>> saveUser(@RequestBody @Valid UserRequest userRequest){
        return userService.createUser(userRequest)
            .then(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Void>> updtadeUser(@PathVariable String id , @RequestBody @Valid UserRequest userRequest){
        return userService.updateUser(id,userRequest)
            .then(Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED)));
    }
    
    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable @Valid String id){
        return userService.deleteUser(id)
            .then(Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED)));
    }

    @PostMapping("/auth/verify")
    public Mono<ResponseEntity<UserAuthResponse>> verifyUserCredentials(@RequestBody UserAuthRequest userAuthRequest){
        System.out.println("Hello world");
        return userService.verifyUserCredentials(userAuthRequest)
            .map(userResponse -> new ResponseEntity<>(userResponse, HttpStatus.OK));
    }


     
}
