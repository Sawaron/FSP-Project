package com.codeandpray.auth.service;

import com.codeandpray.auth.dto.AuthResponse;
import com.codeandpray.auth.dto.LoginRequest;
import com.codeandpray.auth.dto.RegisterRequest;
import com.codeandpray.auth.entity.User;
import com.codeandpray.auth.repository.UserRepository;
import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.security.JwtService;
import com.codeandpray.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Clock;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Clock clock;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = User.normalizeEmail(request.email());

        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw BusinessException.badRequest(
                    "Пароль должен занимать не более 72 байт UTF-8"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw BusinessException.conflict(
                    "Пользователь с таким email уже существует"
            );
        }

        User user = User.registerAthlete(
                email,
                passwordEncoder.encode(request.password()),
                clock.instant()
        );

        userRepository.saveAndFlush(user);

        return response(new SecurityUser(user));
    }

    public AuthResponse login(LoginRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadCredentialsException(
                    "Некорректные учётные данные"
            );
        }

        var authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        User.normalizeEmail(request.email()),
                        request.password()
                )
        );

        return response((SecurityUser) authentication.getPrincipal());
    }

    private AuthResponse response(SecurityUser user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}