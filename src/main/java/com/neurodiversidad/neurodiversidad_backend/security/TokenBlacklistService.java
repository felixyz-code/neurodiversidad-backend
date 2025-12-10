package com.neurodiversidad.neurodiversidad_backend.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // token → expiration
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, Instant expiration) {
        blacklist.put(token, expiration);
    }

    public boolean isBlacklisted(String token) {
        Instant exp = blacklist.get(token);
        if (exp == null) return false;

        // Si ya expiró, se elimina
        if (exp.isBefore(Instant.now())) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }
}
