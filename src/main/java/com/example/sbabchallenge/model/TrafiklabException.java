package com.example.sbabchallenge.model;

import org.springframework.http.HttpStatusCode;

public class TrafiklabException extends RuntimeException{

    private final HttpStatusCode statusCode;
    public TrafiklabException(HttpStatusCode statusCode) {
        super("TrafiklabException, statusCode=" + statusCode);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
