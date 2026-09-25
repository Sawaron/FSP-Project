package com.codeandpray.security;

import com.codeandpray.auth.entity.User;
import com.codeandpray.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public SecurityUser loadUserByUsername(String email) {
        return new SecurityUser(repository.findByEmail(User.normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));
    }

    public SecurityUser loadById(long id) {
        return new SecurityUser(repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));
    }
}