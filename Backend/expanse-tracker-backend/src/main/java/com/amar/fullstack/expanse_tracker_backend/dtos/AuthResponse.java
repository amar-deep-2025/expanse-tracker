package com.amar.fullstack.expanse_tracker_backend.dtos;

public class AuthResponse {

    private String token;
    private String email;
    private String name;
    private String role;

    public AuthResponse(){}

    public AuthResponse(String token, String name,String email, String role) {
        this.token = token;
        this.name=name;
        this.email = email;
        this.role = role;
    }

}
