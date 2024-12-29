package org.driventask.user.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import org.driventask.user.Enum.ERole;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInitializer implements CommandLineRunner{
    
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0 ; i < 10 ; i++){
            log.info("Hello " + i);
            userService.createUser(new UserRequest(
                "full name" + i,
                "admin" + i + "@gmail.com",
                "admin" + i,
                Set.of(ERole.ROLE_USER, ERole.ROLE_ADMIN)
            )).subscribe(
                    null, // onNext is null because it's a Mono<Void>
                    error -> log.error("Error creating user: " + error.getMessage()),
                    () -> log.info("User creation completed for admin" + "@gmail.com")
            );
        }
    }
    
}
