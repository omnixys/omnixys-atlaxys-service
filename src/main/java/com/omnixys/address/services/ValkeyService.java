package com.omnixys.address.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.omnixys.address.models.dto.SignupAddressCacheDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ValkeyService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    public SignupAddressCacheDTO getSignupAddressToken(String token) {

        String key = "verification:signup:address:" + token;

        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            throw new IllegalArgumentException("Signup token expired or invalid");
        }

        try {
            return objectMapper.readValue(json, SignupAddressCacheDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token payload", e);
        }
    }

    public void deleteToken(String token) {
        redisTemplate.delete("verification:signup:address:" + token);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}