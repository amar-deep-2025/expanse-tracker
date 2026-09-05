package com.amar.fullstack.expanse_tracker_backend.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args){
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        System.out.println("Hash "+encoder.encode("123456"));
    }
}
