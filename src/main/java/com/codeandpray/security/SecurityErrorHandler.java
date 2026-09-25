package com.codeandpray.security;

import com.codeandpray.common.exception.ApiError;
import com.codeandpray.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        response.setHeader("WWW-Authenticate", "Bearer");
        write(response, 401, ErrorCode.UNAUTHORIZED, "Требуется действующая авторизация");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        write(response, 403, ErrorCode.FORBIDDEN, "Недостаточно прав");
    }

    public void internalError(HttpServletResponse response) throws IOException {
        write(response, 500, ErrorCode.INTERNAL_ERROR, "Внутренняя ошибка сервера");
    }

    private void write(HttpServletResponse response, int status,
                       ErrorCode code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        mapper.writeValue(response.getOutputStream(), ApiError.of(status, code, message));
    }
}