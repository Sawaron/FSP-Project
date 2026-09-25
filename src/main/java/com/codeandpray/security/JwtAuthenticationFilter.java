package com.codeandpray.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SecurityErrorHandler errors;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "POST".equals(request.getMethod())
                && ("/api/auth/login".equals(path) || "/api/auth/register".equals(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            long id = jwtService.extractUserId(header.substring(7));
            SecurityUser user = userDetailsService.loadById(id);
            if (!user.isEnabled()) {
                throw new JwtException("Account is disabled");
            }
            user.eraseCredentials();
            var authentication = UsernamePasswordAuthenticationToken.authenticated(
                    user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            SecurityContextHolder.clearContext();
            errors.commence(request, response, new BadCredentialsException("Invalid token"));
            return;
        } catch (RuntimeException ex) {
            SecurityContextHolder.clearContext();
            log.error("Authentication infrastructure failure", ex);
            errors.internalError(response);
            return;
        }
        chain.doFilter(request, response);
    }
}