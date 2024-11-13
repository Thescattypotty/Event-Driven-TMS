package org.driventask.user.Payload.Response;

import org.driventask.user.Enum.EUserChangement;

public record UserChangingListener(
    String email,
    String fullName,
    EUserChangement eUserChangement
) {
    
}
