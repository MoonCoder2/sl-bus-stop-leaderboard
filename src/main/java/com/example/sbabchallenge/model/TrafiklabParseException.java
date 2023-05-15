package com.example.sbabchallenge.model;

public class TrafiklabParseException extends RuntimeException{
    public TrafiklabParseException(String json, String message, Throwable e) {
        super("TrafiklabParseException, " + message + ", json='" + json+"'", e);
    }

}
