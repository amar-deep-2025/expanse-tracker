package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    @Autowired
    private StringRedisTemplate  redisTemplate;

    public String generateOtp(){
        return String.valueOf(new Random().nextInt(900000)+100000);
    }

    public void saveOtp(String email, String otp){
        redisTemplate.opsForValue().set(email, otp,5, TimeUnit.MINUTES);

    }

    public String getOtp(String email){
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteOtp(String email){
        redisTemplate.delete("OTP:"+email);
    }

    public void sendOtp(String email, String otp){
        System.out.println("OTP"+otp);
    }

    public void saveUserData(RegisterRequest request){
        String key="USER:"+request.getEmail();

        String value=request.getName()+","+
                request.getEmail()+","+
                request.getPassword()+","+
                request.getPhone();

        redisTemplate.opsForValue().set(key,value,5,TimeUnit.MINUTES);
    }
    public boolean verifyOtp(String key, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            return false;
        }
        if (storedOtp.equals(otp)) {
            redisTemplate.delete(key); // ✅ one-time use
            return true;
        }

        return false;
    }
}
