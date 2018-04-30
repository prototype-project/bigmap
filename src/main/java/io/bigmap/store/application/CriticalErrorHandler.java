package io.bigmap.store.application;

import io.bigmap.store.CriticalError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;


@ControllerAdvice
public class CriticalErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { CriticalError.class })
    protected ResponseEntity<Object> handleConflict(Exception error, WebRequest request) {
        return handleExceptionInternal(error, new HashMap<>(),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
