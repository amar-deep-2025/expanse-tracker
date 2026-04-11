package com.amar.fullstack.expanse_tracker_backend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.UNAUTHORIZED, reason = "Invalid email or password")
public class InvalidCredentialsExceptions extends RuntimeException{
    public InvalidCredentialsExceptions() {
        super("Invalid email or password");
    }

    public InvalidCredentialsExceptions(String message) {
        super(message);
    }

}
