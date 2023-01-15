package com.example.backend.user.domain;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;

@Component
public class UserCounter {
    private static final long TOKEN_VALID_TIME = 30 * 60 * 1000L;
    private final Map<String, Long> loggedInUserCounter = ExpiringMap.builder()
            .maxSize(10000)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(TOKEN_VALID_TIME, TimeUnit.SECONDS)
            .build();

    public long getLoggedInUserCount() {
        return loggedInUserCounter.size();
    }

    public void loginNewUser(long userId, String token) {
        loggedInUserCounter.put(token, userId);
    }

    public void logoutUser(String token) {
        loggedInUserCounter.remove(token);
    }
}
