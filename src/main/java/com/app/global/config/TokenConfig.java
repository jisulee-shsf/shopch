package com.app.global.config;

import com.app.global.jwt.service.TokenManager;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class TokenConfig {

    @Value("${token.access-token-expiration-duration}")
    private Long accessTokenExpirationTime;

    @Value("${token.refresh-token-expiration-duration}")
    private Long refreshTokenExpirationTime;

    @Value("${token.secret}")
    private String tokenSecret;

    @Bean
    public TokenManager tokenManager(Clock clock) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(tokenSecret));
        return new TokenManager(accessTokenExpirationTime, refreshTokenExpirationTime, secretKey, clock);
    }
}
