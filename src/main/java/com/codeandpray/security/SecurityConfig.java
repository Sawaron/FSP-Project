package com.codeandpray.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService users;
    private final SecurityErrorHandler errors;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(errors).accessDeniedHandler(errors))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/contests/*/management/**", "/api/competitions/mine",
                                "/api/competitions/*/management").hasRole("ORGANIZER")
                        .requestMatchers("/api/contests/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/competitions/*/registrations").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.GET, "/api/results/*/management").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.POST, "/api/competitions/*/registrations").hasRole("ATHLETE")
                        .requestMatchers(HttpMethod.PUT, "/api/registrations/*/cancel").hasRole("ATHLETE")
                        .requestMatchers("/api/athletes/me/**").hasRole("ATHLETE")
                        .requestMatchers(HttpMethod.GET,
                                "/api/competitions", "/api/competitions/*",
                                "/api/results", "/api/results/*",
                                "/api/ratings/**", "/api/disciplines/**",
                                "/api/organizations/**", "/api/qualifications/**",
                                "/api/athletes", "/api/athletes/*").permitAll()
                        .requestMatchers("/api/competitions/**", "/api/results/**",
                                "/api/registrations/*/result").hasRole("ORGANIZER")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration() {
        var registration = new FilterRegistrationBean<>(jwtFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}
