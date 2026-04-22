package com.amar.fullstack.expanse_tracker_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,String> restTemplate(RedisConnectionFactory factory){

        RedisTemplate<String,String> template=new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;

    }
}
