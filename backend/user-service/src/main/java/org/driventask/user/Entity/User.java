package org.driventask.user.Entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.driventask.user.Enum.ERole;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
    
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String fullName;

    private String email;

    private String password;

    @Column(value = "user_roles")
    private Set<ERole> roles;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
