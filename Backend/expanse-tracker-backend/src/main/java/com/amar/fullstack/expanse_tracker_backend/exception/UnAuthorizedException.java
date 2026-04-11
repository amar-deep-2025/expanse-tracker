package com.amar.fullstack.expanse_tracker_backend.exception;


import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = org.springframework.http.HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException(String message) {
        super(message);
    }
}
