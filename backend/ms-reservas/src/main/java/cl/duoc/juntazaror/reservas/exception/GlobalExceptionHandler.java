package cl.duoc.juntazaror.reservas.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.servlet.http.HttpServletRequest;

/** Convierte las excepciones en respuestas JSON con el código HTTP correcto. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    // 409: el recurso ya existe o choca con otro (correo repetido, bloque ocupado)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    // Errores de las anotaciones @NotBlank, @Size, @Min, etc. Se devuelve el primero.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Datos inválidos"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, mensaje, request.getRequestURI());
    }

    // Error al llamar a otro microservicio (respuesta con error)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleWebClientResponse(WebClientResponseException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY,
                "Error de comunicación con otro microservicio: " + ex.getStatusCode().value(), request.getRequestURI());
    }

    // El otro microservicio no responde
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleWebClientRequest(WebClientRequestException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY,
                "No se pudo conectar con otro microservicio", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado: " + ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(error, status);
    }
}
