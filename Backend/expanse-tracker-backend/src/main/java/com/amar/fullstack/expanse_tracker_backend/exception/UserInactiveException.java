package com.amar.fullstack.expanse_tracker_backend.exception;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super(message);
    }
}
