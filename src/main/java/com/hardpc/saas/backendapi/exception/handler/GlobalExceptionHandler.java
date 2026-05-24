package com.hardpc.saas.backendapi.exception.handler;

import com.hardpc.saas.backendapi.exception.custom.BusinessException;
import com.hardpc.saas.backendapi.exception.dto.ApiErrorResponse;
import com.hardpc.saas.backendapi.exception.dto.FieldErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. VALIDACIONES (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidations(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorDTO> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldErrorDTO(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());

        return buildResponse(HttpStatus.BAD_REQUEST, "Validación Fallida", "ERR_VALIDATION", "Errores en los campos enviados", request, errores);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldErrorDTO> errores = ex.getConstraintViolations().stream()
                .map(err -> new FieldErrorDTO(err.getPropertyPath().toString(), err.getMessage()))
                .collect(Collectors.toList());

        return buildResponse(HttpStatus.BAD_REQUEST, "Validación de Restricción Fallida", "ERR_CONSTRAINT", "Parámetros inválidos", request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Cuerpo de Petición Inválido", "ERR_MALFORMED_JSON", "El JSON enviado tiene formato incorrecto o tipos de datos inválidos.", request, null);
    }

    // 2. NEGOCIO Y CONFLICTOS (Status Dinámico)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return buildResponse(ex.getStatus(), ex.getStatus().getReasonPhrase(), ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Argumento Inválido", "ERR_ILLEGAL_ARGUMENT", ex.getMessage(), request, null);
    }

    // 3. BASE DE DATOS (409 Conflict)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDatabaseConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflicto de Datos", "ERR_DB_CONFLICT", "La operación viola restricciones de la base de datos (ej. duplicados o llaves foráneas).", request, null);
    }

    // 4. RUTAS Y RECURSOS NO ENCONTRADOS (404 Not Found)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso No Encontrado", "ERR_NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Ruta No Encontrada", "ERR_ENDPOINT_NOT_FOUND", "El endpoint " + ex.getHttpMethod() + " " + ex.getRequestURL() + " no existe.", request, null);
    }

    // 5. SEGURIDAD (401 Unauthorized / 403 Forbidden)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales Inválidas", "ERR_BAD_CREDENTIALS", "Usuario o contraseña incorrectos.", request, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "No Autenticado", "ERR_UNAUTHORIZED", "Token JWT faltante, inválido o expirado.", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acceso Denegado", "ERR_FORBIDDEN", "No tienes los permisos necesarios para realizar esta acción.", request, null);
    }

    // 6. FALLBACK PARA ERRORES NO CONTROLADOS (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Error interno no controlado en la ruta {}: ", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error Interno del Servidor", "ERR_INTERNAL_SERVER", "Ha ocurrido un error inesperado. Por favor, contacte al soporte.", request, null);
    }

    // Método utilitario para construir las respuestas
    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String error, String errorCode, String message, HttpServletRequest request, List<FieldErrorDTO> detalles) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .status(status.value())
                .error(error)
                .errorCode(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .detalles(detalles)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}