package io.bigmap.router;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class ClientExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ClientException.class)
    ResponseEntity<String> handleClientException(ClientException error, WebRequest request) {
        // todo metrics and logs
        return new ResponseEntity<>(error.getMessage(), new HttpHeaders(), error.getHttpStatus());
    }
}
