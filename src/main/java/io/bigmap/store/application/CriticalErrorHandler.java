package io.bigmap.store.application;

import io.bigmap.common.CriticalError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
class CriticalErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CriticalError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String handleCriticalError(Exception error, WebRequest request) {
        // todo metrics and logs
        return "INTERNAL_SERVER_ERROR";
    }
}
