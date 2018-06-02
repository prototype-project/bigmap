package io.bigmap.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
class CriticalErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CriticalError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<String> handleCriticalError(Exception error, WebRequest request) {
        // todo metrics and logs
        return new ResponseEntity<>("INTERNAL_SERVER_ERROR", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
