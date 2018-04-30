package io.bigmap.store.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class ResponseDetails {

    @NotEmpty
    private final int code;

    @NotEmpty
    private final String message;

    @JsonCreator
    ResponseDetails(@JsonProperty("code") int code, @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
