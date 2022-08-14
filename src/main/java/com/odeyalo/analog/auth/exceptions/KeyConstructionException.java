package com.odeyalo.analog.auth.exceptions;

public class KeyConstructionException extends Exception {
    public KeyConstructionException(String message) {
        super(message);
    }

    public KeyConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
