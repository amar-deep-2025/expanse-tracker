package com.amar.fullstack.expanse_tracker_backend.dtos;


import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponseDto {

    private String message;
    private int status;
    private String path;
    private LocalDateTime timeStamp;
    private Map<String,String> errors;


    public ErrorResponseDto(){}

    public ErrorResponseDto(String message,int status,String path){
        this.message=message;
        this.status=status;
        this.path=path;
        this.timeStamp=LocalDateTime.now();

    }
    public ErrorResponseDto(String message,int status,String path,Map<String,String> errors){
        this.message=message;
        this.status=status;
        this.path=path;
        this.timeStamp=LocalDateTime.now();
        this.errors=errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
