package com.amar.fullstack.expanse_tracker_backend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.CONFLICT)
public class UserAllreadyExistsException extends RuntimeException {

    public UserAllreadyExistsException(String message) {
        super(message);
    }
}
