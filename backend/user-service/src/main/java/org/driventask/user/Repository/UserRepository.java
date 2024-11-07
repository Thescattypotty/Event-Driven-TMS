package org.driventask.user.Repository;

import java.util.UUID;

import org.driventask.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,UUID>{
    
}
