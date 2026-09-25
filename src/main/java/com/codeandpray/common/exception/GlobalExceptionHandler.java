package com.codeandpray.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Set<String> DUPLICATES = Set.of(
            "users_email_key", "uk_users_email_normalized",
            "registrations_competition_id_athlete_id_key",
            "uk_registrations_comp_athlete",
            "results_registration_id_key", "uk_results_registration"
    );

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> application(ApplicationException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiError.of(ex.getStatus().value(), ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> forbidden(AccessDeniedException ex) {
        return error(403, ErrorCode.FORBIDDEN, "Недостаточно прав");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> unauthorized(AuthenticationException ex) {
        return error(401, ErrorCode.UNAUTHORIZED, "Не удалось подтвердить учётные данные");
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class,
            PessimisticLockingFailureException.class})
    public ResponseEntity<ApiError> concurrent(RuntimeException ex) {
        return error(409, ErrorCode.CONFLICT, "Данные изменились или заняты другой операцией. Повторите запрос");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integrity(DataIntegrityViolationException ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException violation
                    && DUPLICATES.contains(String.valueOf(violation.getConstraintName()))) {
                return error(409, ErrorCode.CONFLICT, "Такая запись уже существует");
            }
            cause = cause.getCause();
        }
        log.error("Unexpected database constraint violation", ex);
        return error(500, ErrorCode.INTERNAL_ERROR, "Внутренняя ошибка сервера");
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        if (status.is5xxServerError()) {
            log.error("MVC request failed", ex);
        }
        ErrorCode code = status.value() == 400
                ? ErrorCode.VALIDATION_ERROR
                : status.is5xxServerError() ? ErrorCode.INTERNAL_ERROR : ErrorCode.BAD_REQUEST;
        String message = status.is5xxServerError()
                ? "Внутренняя ошибка сервера" : "Некорректный запрос";
        if (status.value() == 404) {
            code = ErrorCode.NOT_FOUND;
            message = "Ресурс не найден";
        }
        return new ResponseEntity<>(ApiError.of(status.value(), code, message), headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unexpected(Exception ex) {
        log.error("Unhandled request failure", ex);
        return error(500, ErrorCode.INTERNAL_ERROR, "Внутренняя ошибка сервера");
    }

    private ResponseEntity<ApiError> error(int status, ErrorCode code, String message) {
        return ResponseEntity.status(status).body(ApiError.of(status, code, message));
    }
}