package org.driventask.user.Component;

import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.driventask.user.Enum.ERole;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner{
    
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0 ; i < 10 ; i++){
            System.out.println("Hello " + i);
            userService.createUser(new UserRequest(
                "full name" + i,
                "admin" + i + "@gmail.com",
                "admin" + i,
                Set.of(ERole.ROLE_USER, ERole.ROLE_ADMIN)
            )).subscribe(
                    null, // onNext is null because it's a Mono<Void>
                    error -> System.err.println("Error creating user: " + error.getMessage()),
                    () -> System.out.println("User creation completed for admin" + "@gmail.com")
            );
        }
    }
    
}
