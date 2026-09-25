package com.codeandpray.common.security;

import com.codeandpray.auth.enums.UserRole;
import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.security.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentActor {
    public SecurityUser getPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof SecurityUser user)) {
            throw BusinessException.unauthorized("Требуется авторизация");
        }
        return user;
    }

    public Long getUserId() {
        return getPrincipal().getId();
    }

    public long requireOrganizerId() {
        return requireRole(UserRole.ORGANIZER);
    }

    public long requireAthleteUserId() {
        return requireRole(UserRole.ATHLETE);
    }

    private long requireRole(UserRole role) {
        SecurityUser user = getPrincipal();
        if (user.getRole() != role) {
            throw BusinessException.forbidden("Недостаточно прав");
        }
        return user.getId();
    }
}