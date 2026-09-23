package cosplayin.app.core.exception.handler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cosplayin.app.core.exception.model.FatalErrorExceptions;
import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.core.exception.model.ResourceConflictExceptions;
import cosplayin.app.core.exception.model.UnauthorizedAccessExceptions;
import cosplayin.app.core.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionsHandlers {

        @ExceptionHandler(UnauthorizedAccessExceptions.class)
        public ResponseEntity<ErrorResponse<String>> handleUnauthorizedExceptions(UnauthorizedAccessExceptions ex) {
                ResponseCookie cookie = ResponseCookie.from("access_token", "")
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(0)
                                .sameSite("lax")
                                .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(
                                                ErrorResponse.<String>builder()
                                                                .error(ex.getMessage())
                                                                .timestamp(Instant.now())
                                                                .build());
        }

        @ExceptionHandler(ForbiddenAccessExceptions.class)
        public ResponseEntity<ErrorResponse<String>> handleForbiddenAccessExceptions(ForbiddenAccessExceptions ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .timestamp(Instant.now())
                                                .build());
        }

        @ExceptionHandler(FatalErrorExceptions.class)
        public ResponseEntity<ErrorResponse<String>> handleInternalServerError(FatalErrorExceptions ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .timestamp(Instant.now())
                                                .build());
        }

        @ExceptionHandler({ RequestValidationException.class, HttpMessageNotReadableException.class })
        public ResponseEntity<ErrorResponse<String>> handleBadRequestsException(Exception ex) {

                String message = switch (ex) {
                        case RequestValidationException e -> e.getMessage();
                        case HttpMessageNotReadableException _ -> "invalid requets body";
                        default -> "Bad request";
                };
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                ErrorResponse.<String>builder()
                                                .error(message)
                                                .timestamp(Instant.now())
                                                .build());
        }

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorResponse<String>> handleNotFoundException(NotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .timestamp(Instant.now())
                                                .build());
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse<String>> handleInvalidArgumentException(
                        MethodArgumentTypeMismatchException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .timestamp(Instant.now())
                                                .build());
        }

        @ExceptionHandler(ResourceConflictExceptions.class)
        public ResponseEntity<ErrorResponse<String>> handleConflictResourceExceptions(
                        ResourceConflictExceptions ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .timestamp(Instant.now())
                                                .build());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidation(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest().body(errors);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse<String>> handleResourceNotFound(NoResourceFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .timestamp(Instant.now())
                                                .build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse<String>> handleUncaughtExceptions(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ErrorResponse.<String>builder()
                                                .error(ex.getMessage())
                                                .timestamp(Instant.now())
                                                .build());
        }

}
