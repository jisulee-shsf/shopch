package com.app.global.jwt.service;

import com.app.domain.member.constant.Role;
import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.AuthenticationScheme;
import com.app.global.jwt.constant.TokenType;
import com.app.global.jwt.dto.TokenResponse;
import com.app.global.jwt.validator.ClaimsValidator;
import com.app.global.resolver.MemberInfoDto;
import com.app.global.util.DateTimeUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Component
public class TokenManager {

    private static final String CLAIM_MEMBER_ID_KEY = "memberId";
    private static final String CLAIM_ROLE_KEY = "role";

    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final SecretKey secretKey;
    private final ClaimsValidator claimsValidator;
    private final Clock jwtClock;

    public TokenManager(@Value("${jwt.access-token-validity}") long accessTokenValidityInMilliseconds,
                        @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInMilliseconds,
                        @Value("${jwt.base64-encoded-token-secret}") String tokenSecret,
                        ClaimsValidator claimsValidator,
                        Clock jwtClock
    ) {
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
        this.claimsValidator = claimsValidator;
        this.jwtClock = jwtClock;
    }

    public TokenResponse createTokenResponse(Long memberId, Role role, Date issueDate) {
        String accessToken = createAccessToken(memberId, role, issueDate);
        LocalDateTime accessTokenExpirationDateTime = extractExpirationDateTime(accessToken);

        String refreshToken = createRefreshToken(memberId, issueDate);
        LocalDateTime refreshTokenExpirationDateTime = extractExpirationDateTime(refreshToken);

        return TokenResponse.builder()
                .authenticationScheme(AuthenticationScheme.BEARER.getText())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(accessTokenExpirationDateTime)
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();
    }

    public String createAccessToken(Long memberId, Role role, Date issueDate) {
        return createToken(memberId, role, issueDate, TokenType.ACCESS, accessTokenValidityInMilliseconds);
    }

    public void validateAccessToken(String accessToken) {
        validateToken(accessToken, TokenType.ACCESS);
    }

    public void validateRefreshToken(String refreshToken) {
        validateToken(refreshToken, TokenType.REFRESH);
    }

    public LocalDateTime extractExpirationDateTime(String token) {
        Claims claims = parseClaims(token);
        Date expirationDate = claims.getExpiration();
        claimsValidator.validateExpiration(expirationDate);

        return DateTimeUtils.convertDateToLocalDateTime(expirationDate);
    }

    public MemberInfoDto extractMemberInfo(String accessToken) {
        Claims claims = parseClaims(accessToken);
        Long memberId = claims.get(CLAIM_MEMBER_ID_KEY, Long.class);
        String role = claims.get(CLAIM_ROLE_KEY, String.class);
        claimsValidator.validateMemberInfo(memberId, role);

        return MemberInfoDto.builder()
                .id(memberId)
                .role(Role.from(role))
                .build();
    }

    private String createRefreshToken(Long memberId, Date issueDate) {
        return createToken(memberId, null, issueDate, TokenType.REFRESH, refreshTokenValidityInMilliseconds);
    }

    private String createToken(Long memberId, Role role, Date issueDate, TokenType tokenType, long validityInMilliseconds) {
        Date expirationDate = new Date(issueDate.getTime() + validityInMilliseconds);
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(tokenType.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim(CLAIM_MEMBER_ID_KEY, memberId)
                .signWith(secretKey);

        if (Objects.nonNull(role)) {
            jwtBuilder.claim(CLAIM_ROLE_KEY, role);
        }
        return jwtBuilder.compact();
    }

    private void validateToken(String token, TokenType expectedTokenType) {
        Claims claims = parseClaims(token);
        String tokenType = claims.getSubject();
        claimsValidator.validateTokenType(tokenType, expectedTokenType);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .clock(jwtClock)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorType.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new AuthenticationException(ErrorType.INVALID_TOKEN);
        }
    }
}
