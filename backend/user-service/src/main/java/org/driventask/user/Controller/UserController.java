package org.driventask.user.Controller;

import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserResponse;
import org.driventask.user.Service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
        return  ResponseEntity.ok(userService.getUser(id));
    }
    
    @PostMapping("/create")
    public ResponseEntity<Void> saveUser(@RequestBody @Valid UserRequest userRequest){
        userService.createUser(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updtadeUser(@PathVariable String id , @RequestBody @Valid UserRequest user){
        userService.updateUser(id, user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Valid String id){
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    

}
