package io.bigmap.router;

import org.springframework.http.HttpStatus;

class ClientException extends RuntimeException {
    private final HttpStatus httpStatus;

    ClientException(String message, Exception e, HttpStatus httpStatus) {
        super(message, e);
        this.httpStatus = httpStatus;
    }

    HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
