package org.driventask.user.Repository;

import java.util.UUID;

import org.driventask.user.Entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends R2dbcRepository<User,UUID>{
}
