package com.app.domain.token.service;

import com.app.domain.token.entity.RefreshToken;
import com.app.domain.token.repository.RefreshTokenRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    @Transactional
    public void registerRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public Optional<RefreshToken> findRefreshTokenByMemberId(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId);
    }

    public RefreshToken getRefreshTokenByMemberId(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    public RefreshToken getRefreshTokenByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        validateRefreshTokenExpiration(refreshToken, LocalDateTime.now(clock));
        return refreshToken;
    }

    private void validateRefreshTokenExpiration(RefreshToken refreshToken, LocalDateTime now) {
        if (refreshToken.isExpired(now)) {
            throw new AuthenticationException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }
}
