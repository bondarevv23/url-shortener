package com.github.bondarevv23.urlshortener.core.aop;

import com.github.bondarevv23.urlshortener.core.exception.AliasAlreadyExistsException;
import com.github.bondarevv23.urlshortener.core.exception.UnknownAliasException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::toString).toList();
        Map<String, Object> body = new HashMap<>();
        body.put("message", "invalid request passed");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({AliasAlreadyExistsException.class})
    public ResponseEntity<?> aliasAlreadyExistsExceptionHandler(AliasAlreadyExistsException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("alias", ex.getAlias());
        body.put("message", "this alias already exists");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({UnknownAliasException.class})
    public ResponseEntity<?> unknownAliasExceptionHandler(UnknownAliasException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("alias", ex.getAlias());
        body.put("message", "unknown alias passed");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Void> uncaughtExceptionHandler() {
        return ResponseEntity.internalServerError().build();
    }

}
