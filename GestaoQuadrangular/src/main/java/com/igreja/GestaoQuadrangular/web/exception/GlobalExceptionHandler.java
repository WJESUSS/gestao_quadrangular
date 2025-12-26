package com.igreja.GestaoQuadrangular.web.exception;

import com.igreja.GestaoQuadrangular.web.exception.VisitanteJaCadastradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VisitanteJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleVisitanteJaCadastrado(
            VisitanteJaCadastradoException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", OffsetDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }
}
