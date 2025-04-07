package com.app.global.config;

import com.app.global.jwt.service.TokenManager;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

import static io.jsonwebtoken.io.Decoders.BASE64URL;

@Configuration
public class TokenConfig {

    @Value("${token.access-token-expiration-duration}")
    private Long accessTokenExpirationDuration;

    @Value("${token.refresh-token-expiration-duration}")
    private Long refreshTokenExpirationDuration;

    @Value("${token.secret}")
    private String tokenSecret;

    @Bean
    public TokenManager tokenManager(Clock clock) {
        SecretKey secretKey = Keys.hmacShaKeyFor(BASE64URL.decode(tokenSecret));
        return new TokenManager(accessTokenExpirationDuration, refreshTokenExpirationDuration, secretKey, clock);
    }
}
