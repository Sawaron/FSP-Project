package com.codeandpray.common.security;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.security.SecurityUser;
import com.codeandpray.auth.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentActor {

    public SecurityUser getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser principal)) {
            throw BusinessException.forbidden("Требуется авторизация");
        }
        return principal;
    }

    public Long getUserId() {
        return getPrincipal().getId();
    }

    public long requireOrganizerId() {
        SecurityUser principal = getPrincipal();
        if (principal.getUser().getRole() != UserRole.ORGANIZER) {
            throw BusinessException.forbidden("Действие разрешено только организатору");
        }
        return principal.getId();
    }

    public long requireAthleteUserId() {
        SecurityUser principal = getPrincipal();
        if (principal.getUser().getRole() != UserRole.ATHLETE) {
            throw BusinessException.forbidden("Действие разрешено только спортсмену");
        }
        return principal.getId();
    }
}