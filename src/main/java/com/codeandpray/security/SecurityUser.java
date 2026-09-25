package com.codeandpray.security;

import com.codeandpray.auth.entity.User;
import com.codeandpray.auth.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails, CredentialsContainer {
    @Getter
    private final Long id;
    @Getter
    private final UserRole role;
    private final String email;
    private String passwordHash;
    private final boolean enabled;

    public SecurityUser(User user) {
        id = user.getId();
        role = user.getRole();
        email = user.getEmail();
        passwordHash = user.getPasswordHash();
        enabled = user.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public void eraseCredentials() {
        passwordHash = null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}